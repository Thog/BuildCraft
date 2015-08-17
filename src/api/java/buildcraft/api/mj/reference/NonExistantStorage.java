package buildcraft.api.mj.reference;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;

/** A simple base class that by default does nothing. Useful if you have a tile entity that stores multiple different
 * states internally, but some of them do not implement power storage and use. */
public final class NonExistantStorage implements IMjExternalStorage {
    public static final NonExistantStorage INSTANCE = new NonExistantStorage();

    @Override
    public EnumMjDevice getDeviceType(EnumFacing side) {// Doesn't really matter, as it cannot accept power anyway
        return EnumMjDevice.ENGINE;
    }

    @Override
    public EnumMjPower getPowerType(EnumFacing side) {// Again doesn't really matter, as it cannot be converted
        return EnumMjPower.NONE;
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

    @Override
    public double currentPower(EnumFacing side) {
        return 0;
    }

    @Override
    public double maxPower(EnumFacing side) {
        return 0;
    }
}
