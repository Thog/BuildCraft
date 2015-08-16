package buildcraft.api.mj.reference;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjDeviceType;
import buildcraft.api.mj.EnumMjPowerType;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;

public class DefaultMjExternalStorage implements IMjExternalStorage {
    /* Explicitly only a single method so we can support lambdas easily in the future */
    public interface IConnectionLimiter {
        boolean allowConnection(World world, EnumFacing flowDirection, IMjExternalStorage me, IMjExternalStorage other, boolean flowingIn);
    }

    public static final IConnectionLimiter DEVICE_TYPE_LIMITER = new IConnectionLimiter() {
        @Override
        public boolean allowConnection(World world, EnumFacing flow, IMjExternalStorage thisOne, IMjExternalStorage other, boolean flowingIn) {
            EnumMjDeviceType thisDevice = thisOne.getDeviceType();
            EnumMjDeviceType otherDevice = other.getDeviceType();
            return flowingIn ? thisDevice.acceptsPowerFrom(otherDevice) : thisDevice.givesPowerTo(otherDevice);
        }
    };

    public static final IConnectionLimiter POWER_TYPE_LIMITER = new IConnectionLimiter() {
        @Override
        public boolean allowConnection(World world, EnumFacing flow, IMjExternalStorage thisOne, IMjExternalStorage other, boolean flowingIn) {
            EnumMjPowerType thisDevice = thisOne.getPowerType();
            EnumMjPowerType otherDevice = other.getPowerType();
            return flowingIn ? thisDevice.canConvertFrom(otherDevice) : thisDevice.canConvertTo(otherDevice);
        }
    };

    private IMjInternalStorage storage = null;
    private final double maxPowerTransfered;
    private final EnumMjDeviceType deviceType;
    private final EnumMjPowerType powerType;
    private List<IConnectionLimiter> connectionLimits = Lists.newArrayList();

    public DefaultMjExternalStorage(EnumMjDeviceType type, double maxPowerTransfered) {
        this(type, EnumMjPowerType.NORMAL, maxPowerTransfered);
    }

    public DefaultMjExternalStorage(EnumMjDeviceType deviceType, EnumMjPowerType powerType, double maxPowerTransfered) {
        this.deviceType = deviceType;
        if (deviceType == null) {
            throw new IllegalArgumentException("You must specify which device type this is!");
        }
        this.powerType = powerType;
        if (powerType == null) {
            throw new IllegalArgumentException("You must specify which power type this is!");
        }
        this.maxPowerTransfered = maxPowerTransfered;
        addLimiter(DEVICE_TYPE_LIMITER);
        addLimiter(POWER_TYPE_LIMITER);
    }

    public void removeDefaultLimiters() {
        connectionLimits.clear();
    }

    /** @param limiter The limiter to add. Note that if this has been finalised by invoking
     *            {@link #setInternalStorage(IMjInternalStorage)} then this will throw an
     *            UnsupportedOperationException. */
    public void addLimiter(IConnectionLimiter limiter) {
        connectionLimits.add(limiter);
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
        if (mj < 0) {// Not a way to extract power
            return mj;
        }
        for (IConnectionLimiter limiter : connectionLimits) {
            if (!limiter.allowConnection(world, flowDirection, this, from, true)) {
                return mj;
            }
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
        if (minMj < 0 || maxMj < 0) {// Not a way to insert power
            return 0;
        }
        for (IConnectionLimiter limiter : connectionLimits) {
            if (!limiter.allowConnection(world, flowDirection, this, to, false)) {
                return 0;
            }
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

    /** This must be called after {@link #addLimiter(IConnectionLimiter)} to finalise the limiters */
    @Override
    public void setInternalStorage(IMjInternalStorage storage) {
        if (this.storage == null) {
            this.storage = storage;
            this.connectionLimits = ImmutableList.copyOf(connectionLimits);
        } else {
            throw new IllegalStateException("You cannot set an internal storage when one has already been set!");
        }
    }
}
