/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.pipes;

import java.nio.channels.Pipe;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.StatementSlot;
import buildcraft.api.transport.IStripesHandler;
import buildcraft.api.transport.IStripesHandler.StripesHandlerType;
import buildcraft.api.transport.IStripesPipe;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.utils.BlockUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.internal.pipes.PipeTransportItems;
import buildcraft.transport.internal.pipes.TileGenericPipe;
import buildcraft.transport.statements.ActionPipeDirection;
import buildcraft.transport.utils.TransportUtils;

public class PipeItemsStripes extends Pipe<PipeTransportItems>implements IMjExternalStorage, IStripesPipe {
    private EnumFacing actionDir = null;

    public PipeItemsStripes(Item item) {
        super(new PipeTransportItems(), item);
    }

    @Override
    public void update() {
        super.update();

        if (container.getWorld().isRemote) {
            return;
        }
    }

    public void eventHandler(PipeEventItem.DropItem event) {
        if (container.getWorld().isRemote) {
            return;
        }

        EnumFacing direction = actionDir;
        if (direction == null) {
            direction = event.direction;
        }

        Vec3 p = Utils.convert(container.getPos()).add(Utils.convert(direction));

        ItemStack stack = event.entity.getEntityItem();
        EntityPlayer player = CoreProxy.proxy.getBuildCraftPlayer((WorldServer) getWorld(), Utils.convertFloor(p)).get();

        switch (direction) {
            case DOWN:
                player.rotationPitch = 90;
                player.rotationYaw = 0;
                break;
            case UP:
                player.rotationPitch = 270;
                player.rotationYaw = 0;
                break;
            case NORTH:
                player.rotationPitch = 0;
                player.rotationYaw = 180;
                break;
            case SOUTH:
                player.rotationPitch = 0;
                player.rotationYaw = 0;
                break;
            case WEST:
                player.rotationPitch = 0;
                player.rotationYaw = 90;
                break;
            case EAST:
                player.rotationPitch = 0;
                player.rotationYaw = 270;
                break;
        }

        /** Check if there's a handler for this item type. */
        for (IStripesHandler handler : PipeManager.stripesHandlers) {
            if (handler.getType() == StripesHandlerType.ITEM_USE && handler.shouldHandle(stack)) {
                if (handler.handle(getWorld(), Utils.convertFloor(p), direction, stack, player, this)) {
                    event.entity = null;
                    return;
                }
            }
        }
    }

    @Override
    public void dropItem(ItemStack itemStack, EnumFacing direction) {
        Vec3 p = Utils.convert(container.getPos()).add(Utils.convert(direction));
        InvUtils.dropItems(getWorld(), itemStack, Utils.convertFloor(p));
    }

    @Override
    public LinkedList<IActionInternal> getActions() {
        LinkedList<IActionInternal> action = super.getActions();
        for (EnumFacing direction : EnumFacing.VALUES) {
            if (!container.isPipeConnected(direction)) {
                action.add(BuildCraftTransport.actionPipeDirection[direction.ordinal()]);
            }
        }
        return action;
    }

    @Override
    protected void actionsActivated(Collection<StatementSlot> actions) {
        super.actionsActivated(actions);

        actionDir = null;

        for (StatementSlot action : actions) {
            if (action.statement instanceof ActionPipeDirection) {
                actionDir = ((ActionPipeDirection) action.statement).direction;
                break;
            }
        }
    }

    @Override
    public void sendItem(ItemStack itemStack, EnumFacing direction) {
        Vec3 pos = new Vec3(container.x() + 0.5, container.y() + TransportUtils.getPipeFloorOf(itemStack), container.z() + 0.5);
        pos = pos.add(Utils.convert(direction, 0.25));

        TravelingItem newItem = TravelingItem.make(pos, itemStack);
        transport.injectItem(newItem, direction);
    }

    @Override
    public IIconProvider getIconProvider() {
        return BuildCraftTransport.instance.pipeIconProvider;
    }

    @Override
    public int getIconIndex(EnumFacing direction) {
        return PipeIconProvider.TYPE.Stripes.ordinal();
    }

    @Override
    public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
        if (tile instanceof TileGenericPipe) {
            TileGenericPipe tilePipe = (TileGenericPipe) tile;

            if (tilePipe.pipe instanceof PipeItemsStripes) {
                return false;
            }
        }

        return super.canPipeConnect(tile, side);
    }

    public double receiveEnergy(EnumFacing from, double maxReceive, boolean simulate) {
        if (maxReceive == 0) {
            return 0;
        } else if (simulate) {
            return maxReceive;
        }

        EnumFacing o = actionDir;
        if (o == null) {
            o = getOpenOrientation();
        }

        if (o != null) {
            Vec3 p = Utils.convert(container.getPos());
            p = p.add(Utils.convert(o));
            BlockPos pos = Utils.convertFloor(p);

            if (!BlockUtils.isUnbreakableBlock(getWorld(), pos)) {
                IBlockState state = getWorld().getBlockState(pos);

                ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                EntityPlayer player = CoreProxy.proxy.getBuildCraftPlayer((WorldServer) getWorld(), pos).get();

                for (IStripesHandler handler : PipeManager.stripesHandlers) {
                    if (handler.getType() == StripesHandlerType.BLOCK_BREAK && handler.shouldHandle(stack)) {
                        if (handler.handle(getWorld(), pos, o, stack, player, this)) {
                            return maxReceive;
                        }
                    }
                }

                List<ItemStack> stacks = state.getBlock().getDrops(getWorld(), pos, state, 0);

                if (stacks != null) {
                    for (ItemStack s : stacks) {
                        if (s != null) {
                            sendItem(s, o.getOpposite());
                        }
                    }
                }

                getWorld().setBlockToAir(pos);
            }
        }

        return maxReceive;
    }

    @Override
    public EnumMjDevice getDeviceType(EnumFacing side) {
        return EnumMjDevice.MACHINE;
    }

    @Override
    public EnumMjPower getPowerType(EnumFacing side) {
        return EnumMjPower.REDSTONE;
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return 0;
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        return receiveEnergy(flowDirection, mj, simulate);
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        return 0.5;
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {}

    @Override
    public double currentPower(EnumFacing side) {
        return 0;
    }

    @Override
    public double maxPower(EnumFacing side) {
        return 1;
    }
}
