package buildcraft.robotics.pathfinding;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import buildcraft.core.lib.utils.Utils;

/** Defines a volume that is completely traversable by robots. This is however limited to only take up a single chunk
 * max. (for the sake of speed when processing) */
public class BlockVolume implements IBlockEmptyRequirment {
    public final BlockPos min, max;
    /** Defines the direct connections to other volumes that this volume touches */
    final Set<BlockArea> linkedAreas = Sets.newIdentityHashSet();
    /** Defines paths that are one block thick and potentially snakey (But only have one exit and entrance we are
     * interested in) */
    final Set<BlockPath> specialistPaths = Sets.newIdentityHashSet();

    final Set<RobotPath> dependantPaths = Sets.newIdentityHashSet();

    private boolean isValid = true;

    final WorldNetworkManager manager;

    BlockVolume(WorldNetworkManager manager, BlockPos min, BlockPos max) {
        this.manager = manager;
        this.min = min;
        this.max = max;
    }

    /** If this volume had any areas, it is required that you run {@link #joinAround()} to reconnect them to the new
     * object. */
    BlockVolume joinWithManager(WorldNetworkManager manager) {
        isValid = false;
        BlockVolume volume = new BlockVolume(manager, min, max);
        for (BlockArea area : linkedAreas) {
            // Clear out any references to the areas that may linger
            if (area.one != this) {
                area.one.linkedAreas.remove(area);
            }
            if (area.two != this) {
                area.two.linkedAreas.remove(area);
            }
        }
        // Explicitly don't copy the areas over to the new one, so they can be remade properly by #joinAround()

        for (BlockPath path : specialistPaths) {
            BlockPath joinedPath = path.joinWithManager(manager);
            volume.specialistPaths.add(joinedPath);
        }
        return volume;
    }

    /** It is assumed that the {@link #manager} has been set to a non-null value */
    void joinPaths() {
        List<BlockPath> paths = Lists.newArrayList(specialistPaths);
        specialistPaths.clear();
        for (BlockPath path : paths) {
            manager.remove(path);
            BlockPath newPath = path.joinWithManager(manager);
            if (newPath == path) {
                specialistPaths.add(newPath);
                manager.add(newPath);
                continue;
            }
            if (newPath.one == this) {
                newPath.two.specialistPaths.remove(path);
                newPath.two.specialistPaths.add(newPath);
            } else {
                newPath.one.specialistPaths.remove(path);
                newPath.one.specialistPaths.add(newPath);
            }
            specialistPaths.add(newPath);
            manager.add(newPath);
        }
    }

    public Set<BlockArea> getAreas() {
        return Collections.unmodifiableSet(linkedAreas);
    }

    @Override
    public boolean dependsOnBlock(BlockPos pos) {
        return false;
    }

    @Override
    public void invalidate() {
        isValid = false;
        for (BlockPath path : specialistPaths) {
            path.invalidate();
        }
        specialistPaths.clear();
        for (RobotPath path : dependantPaths) {
            path.invalidate(this);
        }
    }

    void remove(BlockPath path) {
        if (isValid) {
            /* If this is invalid then this must be being called within invalidate and the list will be cleared anyway.
             * Also, this must be within the for loop and will lead to a ConcurrentModificationException if this was
             * removed. */
            specialistPaths.remove(path);
        }
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /** Attempts to make a connection with volumes around itself with BlockArea's. */
    void joinAround() {
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos min = Utils.getMinForFace(face, this.min, this.max);
            BlockPos max = Utils.getMaxForFace(face, this.min, this.max);
            min = min.offset(face);
            max = max.offset(face);
            Set<BlockVolume> volumes = Sets.newHashSet();
            for (BlockPos pos : Utils.allInBoxIncludingCorners(min, max)) {
                BlockVolume volume = manager.volumeMap.get(pos);
                if (volume != null) {
                    volumes.add(volume);
                }
            }
            for (BlockVolume volume : volumes) {
                boolean isConnected = false;
                for (BlockArea area : linkedAreas) {
                    if (area.one == volume || area.two == volume) {
                        isConnected = true;
                    }
                }
                if (isConnected) {
                    continue;
                }

                BlockPos maxMin = Utils.max(min, volume.min);
                BlockPos minMax = Utils.min(max, volume.max);

                BlockPos areaMin = Utils.min(maxMin, minMax);
                BlockPos areaMax = Utils.max(maxMin, minMax);
                BlockArea area = new BlockArea(this, volume, areaMin, areaMax, face.getAxis());
                linkedAreas.add(area);
                volume.linkedAreas.add(area);
            }
        }
    }
}
