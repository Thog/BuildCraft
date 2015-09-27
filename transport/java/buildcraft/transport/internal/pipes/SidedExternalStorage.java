package buildcraft.transport.internal.pipes;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.mj.reference.NonExistantStorage;
import buildcraft.api.transport.pluggable.PipePluggable;

public class SidedExternalStorage implements IMjExternalStorage {
    /**
     * 
     */
    private final TileGenericPipe tileGenericPipe;

    /** @param tileGenericPipe */
    SidedExternalStorage(TileGenericPipe tileGenericPipe) {
        this.tileGenericPipe = tileGenericPipe;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SidedExternalStorage [" + getSidedStorage(null) + "]");
        return builder.toString();
    }

    private IMjExternalStorage getSidedStorage(EnumFacing side) {
        if (side != null && this.tileGenericPipe.hasPipePluggable(side)) {
            PipePluggable pluggable = this.tileGenericPipe.getPipePluggable(side);
            if (pluggable instanceof IMjExternalStorage) {
                return (IMjExternalStorage) pluggable;
            } else if (pluggable.isBlocking(this.tileGenericPipe, side)) {
                return NonExistantStorage.INSTANCE;
            }
        }
        if (this.tileGenericPipe.getPipe().behaviour instanceof IMjExternalStorage) {
            return (IMjExternalStorage) this.tileGenericPipe.getPipe().behaviour;
        }
        return NonExistantStorage.INSTANCE;
    }

    @Override
    public EnumMjDevice getDeviceType(EnumFacing side) {
        return getSidedStorage(side).getDeviceType(side);
    }

    @Override
    public EnumMjPower getPowerType(EnumFacing side) {
        return getSidedStorage(side).getPowerType(side);
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return getSidedStorage(flowDirection).extractPower(world, flowDirection, to, minMj, maxMj, simulate);
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        return getSidedStorage(flowDirection == null ? null : flowDirection.getOpposite()).insertPower(world, flowDirection, from, mj, simulate);
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        return getSidedStorage(flowDirection == null ? null : flowDirection.getOpposite()).getSuction(world, flowDirection);
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {}

    @Override
    public double currentPower(EnumFacing side) {
        return getSidedStorage(side).currentPower(side);
    }

    @Override
    public double maxPower(EnumFacing side) {
        return getSidedStorage(side).maxPower(side);
    }
}
