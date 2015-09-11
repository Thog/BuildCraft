package buildcraft.transport.pipes;

import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.api.transport.event.PipeEventConnection;
import buildcraft.api.transport.event.PipeEventPlayerInteract;
import buildcraft.api.transport.event.PipeEventPowered;
import buildcraft.api.transport.event.PipeEventTick;
import buildcraft.core.lib.utils.NBTUtils;

public class PipeBehaviourWood extends PipeBehaviour {
    private static final String[] textureSuffix = { "/clear", "/filled" };
    private static final double POWER_EXTRACT_SINGLE = 10;
    private static final double MAX_POWER = POWER_EXTRACT_SINGLE * 64;
    private static final int LOSS_DELAY = 200;
    protected static final double POWER_MULTIPLIER = 0;
    protected static final int FLUID_MULTIPLIER = 100;

    private EnumFacing extractionDirection = EnumFacing.UP;
    protected final DefaultMjInternalStorage internalStorage;

    public PipeBehaviourWood(PipeDefinition definition) {
        super(definition);
        internalStorage = new DefaultMjInternalStorage(MAX_POWER, POWER_EXTRACT_SINGLE, LOSS_DELAY, 1);
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("extractionDirection", NBTUtils.writeEnum(extractionDirection));
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        extractionDirection = NBTUtils.readEnum(nbt.getTag("extractionDirection"), EnumFacing.class);
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
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
    public void onPipeConnect(PipeEventConnection.PipeConnect connect) {
        PipeBehaviour other = connect.behaviour;
        if (other instanceof PipeBehaviourWood) {
            // Emerald extends wood, so its fine.
            connect.allow = false;
        }
    }

    @Subscribe
    public void onRecievePower(PipeEventPowered powered) {
        double excess = internalStorage.insertPower(owner.getTile().getWorld(), powered.mj, false);
        powered.used = powered.mj - excess;
    }

    @Subscribe
    public void onTick(PipeEventTick tick) {
        internalStorage.tick(owner.getTile().getWorld());
        if (internalStorage.hasActivated()) {
            double power = internalStorage.extractPower(owner.getTile().getWorld(), POWER_EXTRACT_SINGLE, MAX_POWER, false);
            // TODO (PASS 0): Make wooden pipes extract!
        }
    }

    @Subscribe
    public void onWrench(PipeEventPlayerInteract.WrenchUsed interact) {
        selectNewDirection();
    }

    private void selectNewDirection() {
        int currentIndex = extractionDirection.getIndex();
        for (int i = 1; i < 7; i++) {
            EnumFacing toTest = EnumFacing.values()[(currentIndex + i) % 6];
            if (isValidExtraction(toTest)) {
                extractionDirection = toTest;
                owner.getTile().scheduleRenderUpdate();
                break;
            }
        }
    }

    protected boolean isValidExtraction(EnumFacing face) {
        if (!owner.getTile().isPipeConnected(face)) {
            return false;
        }
        TileEntity tile = owner.getTile().getWorld().getTileEntity(owner.getTile().getPos());
        if (tile instanceof IPipeTile) {
            return false;
        }
        return true;
    }
}
