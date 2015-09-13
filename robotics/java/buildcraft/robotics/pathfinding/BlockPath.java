package buildcraft.robotics.pathfinding;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import buildcraft.core.lib.utils.PathFinding;

/** Defines an in-world path between two block volumes */
public class BlockPath implements IBlockEmptyRequirment {
    public enum PathDirection {
        START_TO_END,
        END_TO_START
    }

    public final BlockVolume one, two;

    public final BlockPos start, end;
    /** Actually used by robots for their path. */
    public final ImmutableMap<PathDirection, ImmutableList<EnumFacing>> path;
    /** Used to determine if this path is invalid at any point. */
    public final ImmutableSet<BlockPos> dependentBlocks;
    /** A list of all the robot paths that depend on this. */
    final Set<RobotPath> dependantPaths = Sets.newIdentityHashSet();

    private boolean isValid = true;

    final WorldNetworkManager manager;

    BlockPath(WorldNetworkManager manager, BlockVolume one, BlockVolume two, BlockPos start, BlockPos end,
            ImmutableMap<PathDirection, ImmutableList<EnumFacing>> path, ImmutableSet<BlockPos> dependentBlocks) {
        this.manager = manager;
        this.one = one;
        this.two = two;
        this.start = start;
        this.end = end;
        this.path = path;
        this.dependentBlocks = dependentBlocks;
    }

    BlockPath(WorldNetworkManager manager, BlockVolume one, BlockVolume two) {
        this.manager = manager;
        start = end = null;
        path = null;
        dependentBlocks = null;

        this.one = one;
        this.two = two;
        PathFinding finder = new PathFinding(manager.world, start, end);
        finder.iterate();
        finder.getResult();
    }

    /** This will ALWAYS create a new one and invalidate this one */
    BlockPath joinWithManager(WorldNetworkManager manager) {
        isValid = false;
        BlockVolume newOne = manager.getVolume(one.min);
        BlockVolume newTwo = manager.getVolume(two.min);
        return new BlockPath(manager, newOne, newTwo, start, end, path, dependentBlocks);
    }

    @Override
    public boolean dependsOnBlock(BlockPos pos) {
        return dependentBlocks.contains(pos);
    }

    @Override
    public void invalidate() {
        isValid = false;
        one.remove(this);
        two.remove(this);
        for (RobotPath robotPath : dependantPaths) {
            robotPath.invalidate(this);
        }
        dependantPaths.clear();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    boolean validate() {
        return true;
    }

    static List<EnumFacing> reverse(List<EnumFacing> list) {
        List<EnumFacing> newList = Lists.newArrayList();
        for (int i = list.size() - 1; i >= 0; i--) {
            newList.add(list.get(i).getOpposite());
        }
        return newList;
    }

    static ImmutableSet<BlockPos> dependantBlocks(BlockPos start, List<EnumFacing> list) {
        Set<BlockPos> set = Sets.newIdentityHashSet();
        BlockPos current = start;
        set.add(current);
        for (EnumFacing face : list) {
            current = current.offset(face);
            set.add(current);
        }
        return ImmutableSet.copyOf(set);
    }
}
