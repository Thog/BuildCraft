package buildcraft.transport.pipes;

import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.core.BCLog;
import buildcraft.api.core.ISerializable;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.api.transport.event.IPipeEvent;
import buildcraft.api.transport.event.IPipeEventAttemptConnectPipe;
import buildcraft.api.transport.event.IPipeEventConnectBlock;
import buildcraft.api.transport.event.IPipeEventDisconnect;
import buildcraft.api.transport.event.IPipeEventPlayerWrench;
import buildcraft.api.transport.event.IPipeEventPowered;
import buildcraft.api.transport.event.IPipeEventTick;
import buildcraft.core.lib.utils.NBTUtils;
import buildcraft.core.lib.utils.NetworkUtils;

import io.netty.buffer.ByteBuf;

public class BehaviourWood extends PipeBehaviour implements ISerializable {
    private static final String[] textureSuffix = { "_clear", "_filled" };
    private static final double POWER_EXTRACT_SINGLE = 10;
    private static final double MAX_POWER = POWER_EXTRACT_SINGLE * 64;
    private static final int LOSS_DELAY = 200;
    protected static final double POWER_MULTIPLIER = 0;
    protected static final int FLUID_MULTIPLIER = 100;

    private EnumFacing extractionDirection = null;
    protected final DefaultMjInternalStorage internalStorage;

    public BehaviourWood(PipeDefinition definition) {
        super(definition);
        internalStorage = new DefaultMjInternalStorage(MAX_POWER, POWER_EXTRACT_SINGLE, LOSS_DELAY, 1);
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("extractionDirection", NBTUtils.writeEnum(extractionDirection));
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
        BCLog.logger.info("DSK|EXTRACT|WRITE|" + id + "|" + extractionDirection);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        extractionDirection = NBTUtils.readEnum(nbt.getTag("extractionDirection"), EnumFacing.class);
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
        BCLog.logger.info("DSK|EXTRACT|READ|" + id + "|" + extractionDirection);
    }

    @Override
    public void writeData(ByteBuf stream) {
        NetworkUtils.writeEnum(stream, extractionDirection);
        BCLog.logger.info("NET|EXTRACT|WRITE|" + id + "|" + extractionDirection);
    }

    @Override
    public void readData(ByteBuf stream) {
        extractionDirection = NetworkUtils.readEnum(stream, EnumFacing.class);
        BCLog.logger.info("NET|EXTRACT|READ|" + id + "|" + extractionDirection);
    }

    @Override
    public int getIconIndex(EnumFacing side) {
        // Icon index 0 is all directions EXCEPT extraction (clear)
        // Icon index 1 is the direction it is extracting from (filled)
        return side == extractionDirection ? 1 : 0;
    }

    @Override
    public String getIconSuffix(int index) {
        return textureSuffix[index];
    }

    @Subscribe
    public void onPipeAttemptConnect(IPipeEventAttemptConnectPipe connect) {
        PipeBehaviour other = connect.getConnectingPipe().getBehaviour();
        if (other instanceof BehaviourWood) {
            // Emerald extends wood, so its fine.
            connect.disallow();
        }
    }

    @Subscribe
    public void onRecievePower(IPipeEventPowered powered) {
        double excess = internalStorage.insertPower(powered.getPipe().getTile().getWorld(), powered.getMj(), false);
        powered.useMj(powered.getMj() - excess, false);
    }

    @Subscribe
    public void onTick(IPipeEventTick tick) {
        internalStorage.tick(tick.getPipe().getTile().getWorld());
        if (internalStorage.hasActivated()) {
            double power = internalStorage.extractPower(tick.getPipe().getTile().getWorld(), POWER_EXTRACT_SINGLE, MAX_POWER, false);
            // TODO (PASS 0): Make wooden pipes extract!
        }
    }

    @Subscribe
    public void disconnectBlock(IPipeEventDisconnect disconnect) {
        if (disconnect.getConnectingSide() == extractionDirection) {
            selectNewDirection(disconnect);
        }
    }

    @Subscribe
    public void connectBlock(IPipeEventConnectBlock connect) {
        if (extractionDirection == null) {
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
        int currentIndex = extractionDirection == null ? -1 : extractionDirection.getIndex();
        for (int i = 1; i < 7; i++) {
            EnumFacing toTest = EnumFacing.values()[(currentIndex + i) % 6];
            if (isValidExtraction(event, toTest)) {
                extractionDirection = toTest;

                event.getPipe().getTile().scheduleRenderUpdate();
                return;
            }
        }
        extractionDirection = null;
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
}
