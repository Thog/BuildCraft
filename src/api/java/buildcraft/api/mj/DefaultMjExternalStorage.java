package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DefaultMjExternalStorage implements IMjExternalStorage {
    private IMjInternalStorage storage = null;
    private final double maxPowerTransfered;
    private final EnumMjDeviceType deviceType;
    private final EnumMjPowerType powerType;

    public DefaultMjExternalStorage(EnumMjDeviceType type, double maxPowerTransfered) {
        this(type, EnumMjPowerType.NORMAL, maxPowerTransfered);
    }

    public DefaultMjExternalStorage(EnumMjDeviceType deviceType, EnumMjPowerType powerType, double maxPowerTransfered) {
        this.deviceType = deviceType;
        this.powerType = powerType;
        if (deviceType == null) {
            throw new IllegalArgumentException("You must specify which type this is!");
        }
        this.maxPowerTransfered = maxPowerTransfered;
    }

    @Override
    public EnumMjDeviceType getDeviceType() {
        return deviceType;
    }

    @Override
    public EnumMjPowerType getPowerType() {
        return powerType;
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        EnumMjDeviceType otherDeviceType = from.getDeviceType();
        if (!otherDeviceType.givesPowerTo(deviceType) || !deviceType.acceptsPowerFrom(otherDeviceType)) {
            return mj;
        }
        EnumMjPowerType otherPowerType = from.getPowerType();
        if (!otherPowerType.canConvertTo(powerType) || !powerType.canConvertFrom(otherPowerType)) {
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
        EnumMjDeviceType otherType = to.getDeviceType();
        if (!otherType.acceptsPowerFrom(deviceType) || !deviceType.givesPowerTo(otherType)) {
            return 0;
        }
        EnumMjPowerType otherPowerType = to.getPowerType();
        if (!otherPowerType.canConvertFrom(powerType) || !powerType.canConvertTo(otherPowerType)) {
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
    public double getSuction(World world, EnumFacing florDirection) {
        double filled = storage.currentPower() / storage.maxPower();
        filled = 1 - filled;
        return filled / getDeviceType().getFlowDivisor();
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
