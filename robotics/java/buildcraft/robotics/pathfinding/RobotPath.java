package buildcraft.robotics.pathfinding;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.BlockPos;

/** Represents an exact path through many Volumes and Paths. */
public class RobotPath {
    /** Points that mark the paths or volumes the robot needs to travel through to get to the destination. */
    private List<BlockPos> positions = Lists.newArrayList();

    /** Paths that this robot path requires to be valid and free to pass. The paths are removed as the robot clears each
     * one, as then it won't need to check if any of them are invalid. */
    private List<BlockPath> dependentPaths = Lists.newArrayList();

    /** Volumes that this robot path requires to be valid and free to pass. The volumes are removed as the robot clears
     * each one, as then it won't need to check if any of them are invalid. */
    private List<BlockVolume> dependantVolumes = Lists.newArrayList();

    private boolean isValid = true;
    private final BlockPos destination;
    private BlockPos target;

    RobotPath(BlockPos current, BlockPos destination) {
        target = current;
        this.destination = destination;
        recalculatePath(current);
    }

    private BlockPos calculateNext() {
        return null;// TODO: Method stub!
    }

    /** @param current
     * @return True if a path could be found. */
    private boolean recalculatePath(BlockPos current) {
        // Clear out any existing dependancies
        for (BlockVolume blockVolume : dependantVolumes) {
            blockVolume.dependantPaths.remove(this);
        }
        for (BlockPath path : dependentPaths) {
            path.dependantPaths.remove(this);
        }
        dependentPaths.clear();
        dependantVolumes.clear();

        // Actually compute the path
        return false;
    }

    /** @param current The current BlockPos the robot is in
     * @return A BlockPos if the path is still clear, But if the path is not clear it will create a new path from the
     *         current location to get to the destination point. Returns null if it is unable to. */
    public BlockPos getNextPoint(BlockPos current) {
        if (isValid) {
            if (target.equals(current)) {
                target = calculateNext();
            }
            return target;
        }
        if (recalculatePath(current)) {
            target = calculateNext();
            return target;
        }
        return null;
    }

    /** Removes a block path if it was invalidated. Will attempt to recompute the path next time
     * {@link #getNextPoint(BlockPos)} is called. */
    void invalidate(BlockPath path) {
        if (path.isValid()) {
            return;
        }
        if (dependentPaths.remove(path)) {
            isValid = false;
        }
    }

    /** Removes a block volume if it was invalidated. Will attempt to recompute the path next time
     * {@link #getNextPoint(BlockPos)} is called */
    void invalidate(BlockVolume volume) {
        if (volume.isValid()) {
            return;
        }
        if (dependantVolumes.remove(volume)) {
            isValid = false;
        }
    }
}
