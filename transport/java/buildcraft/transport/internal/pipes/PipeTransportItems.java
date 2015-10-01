/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.internal.pipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.BCLog;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeAPI;
import buildcraft.api.transport.PipeProperty;
import buildcraft.api.transport.PipeTransport;
import buildcraft.core.DefaultProps;
import buildcraft.core.lib.inventory.ITransactor;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.inventory.Transactor;
import buildcraft.core.lib.inventory.filters.IStackFilter;
import buildcraft.core.lib.inventory.filters.StackFilter;
import buildcraft.core.lib.utils.BlockUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.event.PipeContentsEditableItem;
import buildcraft.transport.event.PipeContentsItem;
import buildcraft.transport.event.PipeEventAdjustSpeed;
import buildcraft.transport.event.PipeEventDropItem;
import buildcraft.transport.event.PipeEventFindDestination;
import buildcraft.transport.event.PipeEventMovementEnter;
import buildcraft.transport.event.PipeEventMovementExit;
import buildcraft.transport.event.PipeEventMovementReachCenter;
import buildcraft.transport.network.PacketPipeTransportItemStackRequest;
import buildcraft.transport.network.PacketPipeTransportTraveler;
import buildcraft.transport.render.tile.PipeRendererItems;
import buildcraft.transport.utils.TransportUtils;

public final class PipeTransportItems extends PipeTransport implements IDebuggable {
    public static final double ITEM_EXTRACT_COST = 10;
    public static final int MAX_PIPE_STACKS = 64;
    public static final int MAX_PIPE_ITEMS = 1024;
    public boolean allowBouncing = false;
    public final TravelerSet items = new TravelerSet(this);

    public PipeTransportItems(IPipeTile tile) {
        super(tile);
    }

    @Override
    public EnumPipeType getPipeType() {
        return EnumPipeType.ITEM;
    }

    public void defaultReajustSpeed(TravelingItem item) {
        float speed = item.getSpeed();

        if (speed > TransportConstants.PIPE_NORMAL_SPEED) {
            speed -= TransportConstants.PIPE_NORMAL_SPEED;
        }

        if (speed < TransportConstants.PIPE_NORMAL_SPEED) {
            speed = TransportConstants.PIPE_NORMAL_SPEED;
        }

        item.setSpeed(speed);
    }

    private void readjustPosition(TravelingItem item) {
        Vec3 middle = Utils.convertMiddle(container.getPos());
        Vec3 littleBitBelow0Point5 = new Vec3(0.49, 0.49, 0.49);
        Vec3 newPos = Utils.clamp(item.pos, middle.subtract(littleBitBelow0Point5), middle.add(littleBitBelow0Point5));

        if (item.input.getAxis() != Axis.Y) {
            newPos = new Vec3(newPos.xCoord, container.getPos().getY() + TransportUtils.getPipeFloorOf(item.getItemStack()), newPos.zCoord);
        }

        item.pos = newPos;
    }

    public void injectItem(TravelingItem item, EnumFacing inputOrientation) {
        if (item.isCorrupted()) {
            // Safe guard - if for any reason the item is corrupted at this
            // stage, avoid adding it to the pipe to avoid further exceptions.
            return;
        }

        item.reset();
        item.input = inputOrientation;

        PipeContentsEditableItem contents = new PipeContentsEditableItem(item.getItemStack(), item.color);
        PipeEventMovementEnter enter = new PipeEventMovementEnter(container.getPipe(), contents, inputOrientation);
        container.getPipe().postEvent(enter);

        item.setItemStack(contents.getStack());

        if (item.getItemStack() == null || item.getItemStack().stackSize <= 0) {
            return;
        }

        PipeEventAdjustSpeed speed = new PipeEventAdjustSpeed(container.getPipe(), contents.uneditable(), item.getSpeed());
        container.getPipe().postEvent(speed);

        item.setSpeed(item.getSpeed());

        readjustPosition(item);

        if (!container.getWorld().isRemote) {
            item.output = resolveDestination(item);
        }

        items.add(item);

        if (!container.getWorld().isRemote) {
            sendTravelerPacket(item, false);

            int itemStackCount = getNumberOfStacks();

            if (itemStackCount >= (MAX_PIPE_STACKS / 2)) {
                groupEntities();
                itemStackCount = getNumberOfStacks();
            }

            if (itemStackCount > MAX_PIPE_STACKS) {
                BCLog.logger.log(Level.WARN, String.format("Pipe exploded at %s because it had too many stacks: %d", container.getPos(), items
                        .size()));
                destroyPipe();
                return;
            }

            int numItems = getNumberOfItems();

            if (numItems > MAX_PIPE_ITEMS) {
                BCLog.logger.log(Level.WARN, String.format("Pipe exploded at %s, because it had too many items: %d", container.getPos(), numItems));
                destroyPipe();
            }
        }
    }

    private void destroyPipe() {
        BlockUtils.explodeBlock(container.getWorld(), container.getPos());
        container.getWorld().setBlockToAir(container.getPos());
    }

    /** Bounces the item back into the pipe without changing the items map.
     *
     * @param item */
    protected void reverseItem(TravelingItem item) {
        if (item.isCorrupted()) {
            // Safe guard - if for any reason the item is corrupted at this
            // stage, avoid adding it to the pipe to avoid further exceptions.
            return;
        }

        item.toCenter = true;
        item.input = item.output.getOpposite();

        PipeContentsEditableItem contents = new PipeContentsEditableItem(item.getItemStack(), item.color);
        PipeEventMovementEnter enter = new PipeEventMovementEnter(container.getPipe(), contents, item.input);
        container.getPipe().postEvent(enter);

        item.setItemStack(contents.getStack());
        item.color = contents.getColor();

        if (item.getItemStack() == null || item.getItemStack().stackSize <= 0) {
            return;
        }

        PipeEventAdjustSpeed speed = new PipeEventAdjustSpeed(container.getPipe(), contents.uneditable(), item.getSpeed());
        container.getPipe().postEvent(speed);
        item.setSpeed(speed.getRawSpeed());

        readjustPosition(item);

        if (!container.getWorld().isRemote) {
            item.output = resolveDestination(item);
        }

        items.unscheduleRemoval(item);

        if (!container.getWorld().isRemote) {
            sendTravelerPacket(item, true);
        }
    }

    public EnumFacing resolveDestination(TravelingItem data) {
        List<EnumFacing> validDestinations = getPossibleMovements(data);

        if (validDestinations.isEmpty()) {
            return null;
        }

        return validDestinations.get(0);
    }

    /** Returns a list of all possible movements, that is to say adjacent implementers of IPipeEntry or
     * TileEntityChest. */
    public List<EnumFacing> getPossibleMovements(TravelingItem item) {
        Map<EnumFacing, TileEntity> potentialDestinations = Maps.newHashMap();
        Set<EnumFacing> destinations = EnumSet.noneOf(EnumFacing.class);

        EnumSet<EnumFacing> sides = EnumSet.complementOf(EnumSet.of(item.input.getOpposite()));
        sides.remove(null);

        for (EnumFacing o : sides) {
            if (container.getPipe().getTransport().outputOpen(o) && canReceivePipeObjects(o, item)) {
                potentialDestinations.put(o, container.getNeighborTile(o));
                destinations.add(o);
            }
        }

        PipeContentsItem contents = new PipeContentsItem(item.getItemStack(), item.color);
        PipeEventFindDestination findDest = new PipeEventFindDestination(container.getPipe(), contents, item.input, potentialDestinations, 6);
        container.getPipe().postEvent(findDest);

        if (allowBouncing && destinations.isEmpty()) {
            if (canReceivePipeObjects(item.input.getOpposite(), item)) {
                destinations.add(item.input.getOpposite());
            }
        }

        List<EnumFacing> result = Lists.newArrayList(destinations);

        Collections.shuffle(result);

        return Lists.newArrayList(destinations);
    }

    private boolean canReceivePipeObjects(EnumFacing o, TravelingItem item) {
        TileEntity entity = container.getNeighborTile(o);

        if (!container.isPipeConnected(o)) {
            return false;
        }

        if (entity instanceof IPipeTile) {
            Pipe pipe = (Pipe) ((IPipeTile) entity).getPipe();
            if (pipe == null) {
                return false;
            }

            // return !pipe.pipe.isClosed() && pipe.pipe.transport instanceof PipeTransportItems;
            return pipe.getTransport().inputOpen(o.getOpposite()) && pipe.getTransport() instanceof PipeTransportItems;
        } else if (entity instanceof IInventory && item.getInsertionHandler().canInsertItem(item, (IInventory) entity)) {
            if (Transactor.getTransactorFor(entity).add(item.getItemStack(), o.getOpposite(), false).stackSize > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateEntity() {
        moveSolids();
    }

    private void moveSolids() {
        items.flush();

        items.iterating = true;
        for (TravelingItem item : items) {
            if (item.getContainer() != this.container) {
                items.scheduleRemoval(item);
                continue;
            }

            EnumFacing face = item.toCenter ? item.input : item.output;
            item.movePosition(Utils.convert(face, item.getSpeed()));

            if ((item.toCenter && middleReached(item)) || outOfBounds(item)) {
                if (item.isCorrupted()) {
                    items.remove(item);
                    continue;
                }

                item.toCenter = false;

                // Reajusting to the middle
                item.pos = Utils.convert(container.getPos()).add(new Vec3(0.5, TransportUtils.getPipeFloorOf(item.getItemStack()), 0.5));

                if (item.output == null) {
                    if (items.scheduleRemoval(item)) {
                        dropItem(item);
                    }
                } else {
                    PipeContentsEditableItem contents = new PipeContentsEditableItem(item.getItemStack(), item.color);
                    PipeEventMovementReachCenter reachCenter = new PipeEventMovementReachCenter(container.getPipe(), contents, item.input,
                            item.output);
                    container.getPipe().postEvent(reachCenter);
                    item.setItemStack(contents.getStack());
                    item.color = contents.getColor();
                }
            } else if (!item.toCenter && endReached(item)) {
                if (item.isCorrupted()) {
                    items.remove(item);
                    continue;
                }

                // TileEntity tile = container.getNeighborTile(item.output, true);

                PipeContentsEditableItem contents = new PipeContentsEditableItem(item.getItemStack(), item.color);
                PipeEventMovementExit exit = new PipeEventMovementExit(container.getPipe(), contents, item.output);
                container.getPipe().postEvent(exit);

                // boolean handleItem = !exit.handled;
                // TODO (PASS 0): FIND OUT WHAT ITEM EVENT #EXIT #HADNLED WAS USED FOR
                // If the item has not been scheduled to removal by the hook
                // if (handleItem && items.scheduleRemoval(item)) {
                // handleTileReached(item, tile);
                // }

            }
        }
        items.iterating = false;
        items.flush();
    }

    private boolean passToNextPipe(TravelingItem item, TileEntity tile) {
        if (tile instanceof IPipeTile) {
            Pipe pipe = (Pipe) ((IPipeTile) tile).getPipe();
            if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof PipeTransportItems) {
                ((PipeTransportItems) pipe.getTransport()).injectItem(item, item.output);
                return true;
            }
        }
        return false;
    }

    private void handleTileReached(TravelingItem item, TileEntity tile) {
        if (passToNextPipe(item, tile)) {
            // NOOP
        } else if (tile instanceof IInventory) {
            if (!container.getWorld().isRemote) {
                if (item.getInsertionHandler().canInsertItem(item, (IInventory) tile)) {
                    ItemStack added = Transactor.getTransactorFor(tile).add(item.getItemStack(), item.output.getOpposite(), true);
                    item.getItemStack().stackSize -= added.stackSize;
                }

                if (item.getItemStack().stackSize > 0) {
                    reverseItem(item);
                }
            }
        } else {
            dropItem(item);
        }
    }

    private void dropItem(TravelingItem item) {
        if (container.getWorld().isRemote) {
            return;
        }

        PipeEventDropItem dropItemEvent = new PipeEventDropItem(container.getPipe(), item.toEntityItem());
        container.getPipe().postEvent(dropItemEvent);

        if (dropItemEvent.getDroppedItem() == null || dropItemEvent.getDroppedItem().getEntityItem().stackSize <= 0) {
            return;
        }

        final EntityItem entity = dropItemEvent.getDroppedItem();
        EnumFacing direction = item.input;
        entity.setPosition(entity.posX + direction.getFrontOffsetX() * 0.5d, entity.posY + direction.getFrontOffsetY() * 0.5d, entity.posZ + direction
                .getFrontOffsetZ() * 0.5d);

        entity.motionX = direction.getFrontOffsetX() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;
        entity.motionY = direction.getFrontOffsetY() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;
        entity.motionZ = direction.getFrontOffsetZ() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;

        container.getWorld().spawnEntityInWorld(entity);
    }

    protected boolean middleReached(TravelingItem item) {
        float middleLimit = item.getSpeed() * 1.01F;
        return Utils.convertMiddle(container.getPos()).subtract(item.pos).lengthVector() < middleLimit;
    }

    protected boolean endReached(TravelingItem item) {
        return item.pos.distanceTo(Utils.convertMiddle(container.getPos())) > 0.5;
        // return item.pos.xCoord > container.getPos().getX() + 1 || item.pos.xCoord < container.x() || item.pos.yCoord
        // > container.y() + 1
        // || item.pos.yCoord < container.y() || item.pos.zCoord > container.z() + 1 || item.pos.zCoord < container.z();
    }

    protected boolean outOfBounds(TravelingItem item) {
        return item.pos.distanceTo(Utils.convertMiddle(container.getPos())) > 1;
        // return item.pos.xCoord > container.x() + 2 || item.pos.xCoord < container.x() - 1 || item.pos.yCoord >
        // container.y() + 2
        // || item.pos.yCoord < container.y() - 1 || item.pos.zCoord > container.z() + 2 || item.pos.zCoord <
        // container.z() - 1;
    }

    public Vec3 getPosition() {
        return Utils.convert(container.getPos());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        NBTTagList nbttaglist = nbt.getTagList("travelingEntities", Constants.NBT.TAG_COMPOUND);

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            try {
                NBTTagCompound dataTag = nbttaglist.getCompoundTagAt(j);

                TravelingItem item = TravelingItem.make(dataTag);

                if (item.isCorrupted()) {
                    continue;
                }

                items.scheduleLoad(item);
            } catch (Throwable t) {
                // It may be the case that entities cannot be reloaded between
                // two versions - ignore these errors.
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        NBTTagList nbttaglist = new NBTTagList();

        for (TravelingItem item : items) {
            NBTTagCompound dataTag = new NBTTagCompound();
            nbttaglist.appendTag(dataTag);
            item.writeToNBT(dataTag);
        }

        nbt.setTag("travelingEntities", nbttaglist);
    }

    protected void doWork() {}

    /** Handles a packet describing a stack of items inside a pipe.
     *
     * @param packet */
    public void handleTravelerPacket(PacketPipeTransportTraveler packet) {
        TravelingItem item = TravelingItem.clientCache.get(packet.getTravelingEntityId());

        if (item == null) {
            item = TravelingItem.make(packet.getTravelingEntityId());
        }

        if (item.getContainer() != container) {
            items.add(item);
        }

        if (packet.forceStackRefresh() || item.getItemStack() == null) {
            BuildCraftTransport.instance.sendToServer(new PacketPipeTransportItemStackRequest(packet.getTravelingEntityId()));
        }

        item.pos = packet.getItemPos();

        item.setSpeed(packet.getSpeed());

        item.toCenter = true;
        item.input = packet.getInputOrientation();
        item.output = packet.getOutputOrientation();
        item.color = packet.getColor();
    }

    private void sendTravelerPacket(TravelingItem data, boolean forceStackRefresh) {
        PacketPipeTransportTraveler packet = new PacketPipeTransportTraveler(data, forceStackRefresh);
        BuildCraftTransport.instance.sendToPlayers(packet, container.getWorld(), container.getPos(), DefaultProps.PIPE_CONTENTS_RENDER_DIST);
    }

    public int getNumberOfStacks() {
        int num = 0;
        for (TravelingItem item : items) {
            if (!item.ignoreWeight()) {
                num++;
            }
        }
        return num;
    }

    public int getNumberOfItems() {
        int num = 0;
        for (TravelingItem item : items) {
            if (!item.ignoreWeight() && item.getItemStack() != null) {
                num += item.getItemStack().stackSize;
            }
        }
        return num;
    }

    protected void neighborChange() {}

    @Override
    public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
        if (tile instanceof IPipeTile) {
            Pipe pipe2 = (Pipe) ((IPipeTile) tile).getPipe();
            if (BlockGenericPipe.isValid(pipe2) && !(pipe2.getTransport() instanceof PipeTransportItems)) {
                return false;
            }
        }

        if (tile instanceof ISidedInventory) {
            int[] slots = ((ISidedInventory) tile).getSlotsForFace(side.getOpposite());
            return slots != null && slots.length > 0;
        }

        return tile instanceof IPipeTile || (tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0);
    }

    /** Group all items that are similar, that is to say same dmg, same id, same nbt and no contribution controlling
     * them */
    public void groupEntities() {
        for (TravelingItem item : items) {
            if (item.isCorrupted()) {
                continue;
            }
            for (TravelingItem otherItem : items) {
                if (item.tryMergeInto(otherItem)) {
                    break;
                }
            }
        }
    }

    @Override
    public void dropContents() {
        groupEntities();

        for (TravelingItem item : items) {
            if (!item.isCorrupted()) {
                InvUtils.dropItems(getWorld(), item.getItemStack(), container.getPos());
            }
        }

        items.clear();
    }

    public List<ItemStack> getDroppedItems() {
        groupEntities();

        ArrayList<ItemStack> itemsDropped = new ArrayList<ItemStack>(items.size());

        for (TravelingItem item : items) {
            if (!item.isCorrupted()) {
                itemsDropped.add(item.getItemStack());
            }
        }

        return itemsDropped;
    }

    @Override
    public boolean delveIntoUnloadedChunks() {
        return true;
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        left.add("");
        left.add("PipeTransportItems");
        left.add("- Items: " + getNumberOfStacks() + "/" + MAX_PIPE_STACKS + " (" + getNumberOfItems() + "/" + MAX_PIPE_ITEMS + ")");
        for (TravelingItem item : items) {
            left.add("");
            left.add("  - " + item.getItemStack());
            left.add("    - pos = " + item.pos);
            left.add("    - middle = " + middleReached(item));
            left.add("    - end = " + endReached(item));
            left.add("    - out of boounds = " + outOfBounds(item));
        }
    }

    @Override
    public List<PipeProperty<?>> getAllProperties() {
        List<PipeProperty<?>> list = Lists.newArrayList();
        list.add(PipeAPI.ITEM_COUNT);
        list.add(PipeAPI.STACK_COUNT);
        list.add(PipeAPI.CONTENTS);
        list.add(PipeAPI.PERCENT_FULL);
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderTransport(float partialTicks) {
        PipeRendererItems.renderItemPipe(container.getPipe(), this, partialTicks);
    }
}
