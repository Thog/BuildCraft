package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/** A simple base class that by default does nothing. Useful if you have a tile entity that stores multiple different
 * states internally, but some of them do not implement power storage and use. */
public final class NonExistantStorage implements IMjExternalStorage {
    public static final NonExistantStorage INSTANCE = new NonExistantStorage();

    @Override
    public EnumMjDeviceType getDeviceType() {// Doesn't really matter, as it cannot accept power anyway
        return EnumMjDeviceType.ENGINE;
    }

    @Override
    public EnumMjPowerType getPowerType() {// Again doesn't really matter, as it cannot be converted
        return EnumMjPowerType.NORMAL;
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return 0;
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        return mj;
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        return 0;
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {}
}
