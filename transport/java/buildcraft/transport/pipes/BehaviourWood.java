package buildcraft.transport.pipes;

import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.core.BCLog;
import buildcraft.api.core.ISerializable;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.api.transport.event.IPipeEvent;
import buildcraft.api.transport.event.IPipeEventAttemptConnectPipe;
import buildcraft.api.transport.event.IPipeEventConnectBlock;
import buildcraft.api.transport.event.IPipeEventDisconnect;
import buildcraft.api.transport.event.IPipeEventPlayerWrench;
import buildcraft.api.transport.event.IPipeEventTick;
import buildcraft.core.lib.inventory.filters.StackFilter;
import buildcraft.core.lib.utils.NBTUtils;
import buildcraft.core.lib.utils.NetworkUtils;
import buildcraft.transport.internal.pipes.PipeTransportItems;

import io.netty.buffer.ByteBuf;

public class BehaviourWood extends PipeBehaviour implements ISerializable, IMjExternalStorage {
    private static final double POWER_EXTRACT_SINGLE = PipeTransportItems.ITEM_EXTRACT_COST;
    private static final double MAX_POWER = POWER_EXTRACT_SINGLE * 64;
    private static final int LOSS_DELAY = 200;
    protected static final double POWER_MULTIPLIER = 0;
    protected static final int FLUID_MULTIPLIER = 100;

    private EnumFacing extractionFace = null;
    protected final DefaultMjInternalStorage internalStorage;

    public BehaviourWood(PipeDefinition definition, IPipeTile pipe) {
        super(definition, pipe);
        internalStorage = new DefaultMjInternalStorage(MAX_POWER, POWER_EXTRACT_SINGLE, LOSS_DELAY, 1);
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("extractionDirection", NBTUtils.writeEnum(extractionFace));
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        extractionFace = NBTUtils.readEnum(nbt.getTag("extractionDirection"), EnumFacing.class);
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
    }

    @Override
    public void writeData(ByteBuf stream) {
        NetworkUtils.writeEnum(stream, extractionFace);
    }

    @Override
    public void readData(ByteBuf stream) {
        extractionFace = NetworkUtils.readEnum(stream, EnumFacing.class);
    }

    @Override
    public int getIconIndex(EnumFacing side) {
        // Icon index 0 is all directions EXCEPT extraction (clear)
        // Icon index 1 is the direction it is extracting from (filled)
        return side == extractionFace ? 1 : 0;
    }

    @Subscribe
    public void onPipeAttemptConnect(IPipeEventAttemptConnectPipe connect) {
        PipeBehaviour other = connect.getConnectingPipe().getBehaviour();
        if (other instanceof BehaviourWood) {
            connect.disallow();
        }
        // if (other instanceof BehaviourEmerald) {
        // connect.disallow();
        // }
    }
    //
    // @Subscribe
    // public void onRecievePower(IPipeEventPowered powered) {
    // double excess = internalStorage.insertPower(powered.getPipe().getTile().getWorld(), powered.getMj(), false);
    // powered.useMj(powered.getMj() - excess, false);
    // }

    @Subscribe
    public void onTick(IPipeEventTick tick) {
        internalStorage.tick(tick.getPipe().getTile().getWorld());
        if (internalStorage.hasActivated() && extractionFace != null) {
            double power = internalStorage.extractPower(tick.getPipe().getTile().getWorld(), POWER_EXTRACT_SINGLE, MAX_POWER, false);
            double leftOver = tick.getPipe().getTransport().extractFromTile(power, extractionFace, StackFilter.ALL);
            internalStorage.insertPower(tick.getPipe().getTile().getWorld(), leftOver, false);
        }
    }

    @Subscribe
    public void disconnectBlock(IPipeEventDisconnect disconnect) {
        if (disconnect.getConnectingSide() == extractionFace) {
            selectNewDirection(disconnect);
        }
    }

    @Subscribe
    public void connectBlock(IPipeEventConnectBlock connect) {
        if (extractionFace == null) {
            selectNewDirection(connect);
        }
    }

    @Subscribe
    public void onWrench(IPipeEventPlayerWrench wrench) {
        selectNewDirection(wrench);
    }

    private void selectNewDirection(IPipeEvent event) {
        if (event.getPipe().getTile().getWorld().isRemote) {
            return;
        }
        int currentIndex = extractionFace == null ? -1 : extractionFace.getIndex();
        for (int i = 1; i < 7; i++) {
            EnumFacing toTest = EnumFacing.values()[(currentIndex + i) % 6];
            if (isValidExtraction(event, toTest)) {
                extractionFace = toTest;

                event.getPipe().getTile().scheduleRenderUpdate();
                return;
            }
        }
        extractionFace = null;
    }

    protected boolean isValidExtraction(IPipeEvent event, EnumFacing face) {
        IPipeTile pipe = event.getPipe().getTile();
        if (!pipe.isPipeConnected(face)) {
            return false;
        }
        TileEntity tile = pipe.getWorld().getTileEntity(pipe.getPos().offset(face));
        if (tile instanceof IPipeTile) {
            return false;
        }
        return true;
    }

    // IMjExternalStorage

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
        return internalStorage.insertPower(world, mj, simulate);
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        return internalStorage.getSuction();
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {}

    @Override
    public double currentPower(EnumFacing side) {
        return internalStorage.currentPower();
    }

    @Override
    public double maxPower(EnumFacing side) {
        return internalStorage.maxPower();
    }
}
