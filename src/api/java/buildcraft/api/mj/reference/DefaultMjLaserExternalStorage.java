package buildcraft.api.mj.reference;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import buildcraft.api.mj.EnumMjDeviceType;
import buildcraft.api.mj.EnumMjPowerType;
import buildcraft.api.mj.IMjLaserStorage;

public abstract class DefaultMjLaserExternalStorage extends DefaultMjExternalStorage implements IMjLaserStorage {
    private final EnumFacing face;
    private final AxisAlignedBB target;

    public DefaultMjLaserExternalStorage(EnumMjDeviceType type, double maxPowerTransfered, EnumFacing face, AxisAlignedBB target) {
        super(type, EnumMjPowerType.LASER, maxPowerTransfered);
        this.face = face;
        this.target = target;
    }

    @Override
    public AxisAlignedBB getTargetBB() {
        return target;
    }

    @Override
    public EnumFacing getTargetFace() {
        return face;
    }
}
