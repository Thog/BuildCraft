package buildcraft.api.mj.reference;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjLaserStorage;

public abstract class DefaultMjLaserExternalStorage extends DefaultMjExternalStorage implements IMjLaserStorage {
    private final EnumFacing face;
    private final AxisAlignedBB target;

    public DefaultMjLaserExternalStorage(EnumMjDevice type, double maxPowerTransfered, EnumFacing face, AxisAlignedBB target) {
        super(type, EnumMjPower.LASER, maxPowerTransfered);
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
