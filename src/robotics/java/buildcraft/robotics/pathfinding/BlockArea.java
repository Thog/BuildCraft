package buildcraft.robotics.pathfinding;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing.Axis;

/** Defines an in-world area that two BlockVolumes are linked by */
public class BlockArea {
    public final BlockVolume one, two;
    public final BlockPos min, max;
    public final Axis axis;

    BlockArea(BlockVolume one, BlockVolume two, BlockPos min, BlockPos max, Axis axis) {
        this.one = one;
        this.two = two;
        this.min = min;
        this.max = max;
        this.axis = axis;
    }
}
