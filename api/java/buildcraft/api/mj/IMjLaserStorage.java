package buildcraft.api.mj;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

/** Implement this on an {@link IMjExternalStorage} to receive laser power from lasers etc. Note that senders do not
 * need to implement this as the methods are all related to recieving */
public interface IMjLaserStorage extends IMjExternalStorage {
    /** Get the target area that the lasers should target, i.e. this is the little box on top of an assembly table. A
     * random point within this will be selected every few seconds for the laser to "hit" */
    AxisAlignedBB getTargetBB();

    /** Get the face of the target area. This is to allow the target to check if it is coming from the right
     * direction. */
    EnumFacing getTargetFace();

    /** See if this tile can currently receive power. This can be used to determine if the tile is can start working (in
     * the case of an assembly table) or if this is a generic tile (like a pipe) which may or may not have a module that
     * actually requires power. */
    boolean canCurrentlyRecievePower();
}
