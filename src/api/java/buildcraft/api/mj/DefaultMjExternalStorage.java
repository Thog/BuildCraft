package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DefaultMjExternalStorage implements IMjExternalStorage {
    private IMjInternalStorage storage = null;
    private final EnumMjType type;

    public DefaultMjExternalStorage(EnumMjType type) {
        this.type = type;
        if (type == null) {
            throw new IllegalArgumentException("You must specify which type this is!");
        }
    }

    @Override
    public EnumMjType getType() {
        return type;
    }

    @Override
    public double recievePower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        EnumMjType otherType = from.getType();
        if (!otherType.givesPowerTo(type) || !type.acceptsPowerFrom(otherType)) {
            return mj;
        }
        return storage.givePower(world, mj, simulate);
    }

    @Override
    public double takePower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        EnumMjType otherType = to.getType();
        if (!otherType.acceptsPowerFrom(type) || !type.givesPowerTo(otherType)) {
            return 0;
        }
        return storage.takePower(world, minMj, maxMj, simulate);
    }

    @Override
    public double getFlow() {
        double filled = storage.currentPower() / storage.maxPower();
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
