package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DefaultMjExternalStorage implements IMjExternalStorage {
    private IMjInternalStorage storage = null;
    private final double maxPowerTransfered;
    private final EnumMjType type;

    public DefaultMjExternalStorage(EnumMjType type, double maxPowerTransfered) {
        this.type = type;
        if (type == null) {
            throw new IllegalArgumentException("You must specify which type this is!");
        }
        this.maxPowerTransfered = maxPowerTransfered;
    }

    @Override
    public EnumMjType getType() {
        return type;
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        EnumMjType otherType = from.getType();
        if (!otherType.givesPowerTo(type) || !type.acceptsPowerFrom(otherType)) {
            return mj;
        }
        if (mj < 0) {// Not a way to extract power
            return mj;
        }
        if (mj <= maxPowerTransfered) {
            return storage.insertPower(world, mj, simulate);
        } else {
            double excess = storage.insertPower(world, maxPowerTransfered, simulate);
            excess += maxPowerTransfered - mj;
            return excess;
        }
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        EnumMjType otherType = to.getType();
        if (!otherType.acceptsPowerFrom(type) || !type.givesPowerTo(otherType)) {
            return 0;
        }
        if (minMj < 0 || maxMj < 0) {// Not a way to insert power
            return 0;
        }
        if (maxMj > maxPowerTransfered) {
            maxMj = maxPowerTransfered;
        }
        if (minMj > maxPowerTransfered) {
            minMj = maxPowerTransfered;
        }
        return storage.extractPower(world, minMj, maxMj, simulate);
    }

    @Override
    public double getSuction() {
        double filled = storage.currentPower() / storage.maxPower();
        filled = 1 - filled;
        return filled / getType().getFlowDivisor();
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {
        if (this.storage == null) {
            this.storage = storage;
        } else {
            throw new IllegalStateException("You cannot set an internal storage when one has already been set!");
        }
    }
}
