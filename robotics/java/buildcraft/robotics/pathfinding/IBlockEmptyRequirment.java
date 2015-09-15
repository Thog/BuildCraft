package buildcraft.robotics.pathfinding;

import net.minecraft.util.BlockPos;

public interface IBlockEmptyRequirment {
    /** Check to see if this requires a specific block or not */
    boolean dependsOnBlock(BlockPos pos);

    /** Invalidates this requirement such that any listeners (Anything that depends on it) will also be invalidated and
     * a research can be done */
    void invalidate();

    boolean isValid();
}
