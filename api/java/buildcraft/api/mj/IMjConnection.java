package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;

/** Any instance of IMjExternalStorage may implement this to have better control over connections. */
public interface IMjConnection {
    /** @param face The face of the block you are testing: if you are trying to connect to an engine below, this would
     *            be EnumFacing.UP.
     * @param from The storage device that is checking if the other one can connect.
     * @return True if this IMjExternalStorage can connect */
    boolean canConnectPower(EnumFacing face, IMjExternalStorage from);
}
