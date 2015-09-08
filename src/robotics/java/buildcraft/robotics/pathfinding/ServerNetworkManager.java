package buildcraft.robotics.pathfinding;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.World;

import buildcraft.api.core.BCLog;
import buildcraft.core.lib.utils.Utils;
import buildcraft.robotics.BuildCraftRobotics;
import buildcraft.robotics.pathfinding.BlockPath.PathDirection;
import buildcraft.robotics.pathfinding.PacketPathfinding.BlockType;
import buildcraft.robotics.pathfinding.PacketPathfinding.ChangeType;

public class ServerNetworkManager extends WorldNetworkManager {
    public static final String SAVED_DATA = "roboticsNetwork";
    public static final int MAX_WORLD_CHECKS = 10000;

    /** A list of all the different blocks that have asked for checks to be performed. These will be */
    private final Set<BlockPos> queuedForChecking = Sets.newHashSet();

    /** Set every time add or remove has been called and reset every tick to track how many block positions to check
     * from {@link #queuedForChecking} */
    private boolean hasChanged = false;
    private boolean hasMadePath = false;
    private int worldChecks = 0;

    ServerNetworkManager(World world) {
        super(world);
        if (world.isRemote) {
            throw new IllegalArgumentException("Tried to create a server on the client!");
        }
        world.addWorldAccess(new WorldNetworkAccessor(this));
        NetworkSavedData data = (NetworkSavedData) world.getPerWorldStorage().loadData(NetworkSavedData.class, SAVED_DATA);
        if (data != null) {
            load(data);
        }
        world.getPerWorldStorage().setData(SAVED_DATA, new NetworkSavedData(this, SAVED_DATA));
    }

    private void load(NetworkSavedData data) {
        List<BlockVolume> volumes = Lists.newArrayList(volumeSet);
        for (BlockVolume volume : volumes) {
            remove(volume);
        }

        List<BlockPath> paths = Lists.newArrayList(pathSet);
        for (BlockPath path : paths) {
            remove(path);
        }

        for (BlockVolume volume : data.volumes) {
            add(volume.joinWithManager(this));
        }

        for (BlockPath path : data.paths) {
            add(path.joinWithManager(this));
        }

        for (BlockVolume volume : volumes) {
            volume.joinAround();
            volume.joinPaths();
        }
    }

    @Override
    void remove(BlockVolume volume) {
        if (volume == null) {
            return;
        }
        hasChanged = true;
        volume.invalidate();
        if (volumeSet.remove(volume)) {
            for (BlockPos pos : Utils.allInBoxIncludingCorners(volume.min, volume.max)) {
                volumeMap.remove(pos);
                queuedForChecking.add(pos);
            }
        }

        PacketPathfinding packet = new PacketPathfinding();
        packet.start = volume.min;
        packet.end = volume.max;
        packet.type = BlockType.VOLUME;
        packet.change = ChangeType.REMOVE;
        BuildCraftRobotics.instance.sendToWorld(packet, world);
    }

    @Override
    void add(BlockVolume volume) {
        if (volume == null) {
            return;
        }
        hasChanged = true;
        super.add(volume);
        // Because the volume was merged with the manager in the super method
        volume = getVolume(volume.min);

        PacketPathfinding packet = new PacketPathfinding();
        packet.start = volume.min;
        packet.end = volume.max;
        packet.type = BlockType.VOLUME;
        packet.change = ChangeType.ADD;
        BuildCraftRobotics.instance.sendToWorld(packet, world);

        add(mergeAround(volume));
    }

    @Override
    void remove(BlockPath path) {
        if (path == null) {
            return;
        }
        hasChanged = true;
        path.invalidate();
        if (pathSet.remove(path)) {
            for (BlockPos pos : path.dependentBlocks) {
                pathMap.remove(pos);
                queuedForChecking.add(pos);
            }
        }

        PacketPathfinding packet = new PacketPathfinding();
        packet.start = path.start;
        packet.end = path.end;
        // We don't need to send over the faces
        packet.faces = ImmutableList.of();
        packet.type = BlockType.PATH;
        packet.change = ChangeType.REMOVE;
        BuildCraftRobotics.instance.sendToWorld(packet, world);
    }

    void add(BlockPath path) {
        if (path == null) {
            return;
        }
        hasChanged = true;
        super.add(path);

        PacketPathfinding packet = new PacketPathfinding();
        packet.start = path.start;
        packet.end = path.end;
        packet.faces = path.path.get(PathDirection.START_TO_END);
        packet.type = BlockType.PATH;
        packet.change = ChangeType.ADD;
        BuildCraftRobotics.instance.sendToWorld(packet, world);
    }

    boolean canTrack(BlockPos pos) {
        worldChecks++;
        return world.isAirBlock(pos);
    }

    boolean canVolumeUse(BlockPos pos) {
        return canTrack(pos) && getVolume(pos) == null;
    }

    private BlockVolume mergeAround(BlockVolume volume) {
        List<BlockVolume> volumes = Lists.newArrayList();
        for (BlockArea area : volume.linkedAreas) {
            if (area.one != volume) {
                volumes.add(area.one);
            }
            if (area.two != volume) {
                volumes.add(area.two);
            }
        }

        BlockPos min = volume.min;
        BlockPos max = volume.max;

        while (!volumes.isEmpty()) {
            BlockVolume testing = volumes.remove(new Random().nextInt(volumes.size()));

            BlockPos testMin = Utils.min(min, testing.min);
            BlockPos testMax = Utils.max(max, testing.max);

            boolean can = true;
            for (BlockPos pos : Utils.allInBoxIncludingCorners(testMin, testMax)) {
                if (!canTrack(pos)) {
                    can = false;
                    break;
                }
            }
            if (!can) {
                continue;
            }
            min = testMin;
            max = testMax;
        }

        if (min.equals(volume.min) && max.equals(volume.max)) {
            return null;
        }

        Set<BlockVolume> checkedVolumes = Sets.newHashSet();

        for (BlockPos pos : Utils.allInBoxIncludingCorners(min, max)) {
            checkedVolumes.add(getVolume(pos));
        }

        checkedVolumes.remove(null);

        for (BlockVolume check : checkedVolumes) {
            BlockPos checkMin = Utils.min(min, check.min);
            BlockPos checkMax = Utils.max(max, check.max);
            if (max.distanceSq(min) > check.max.distanceSq(check.min) * 2) {
                continue;// If the other one is significantly smaller then remove it in the next loop
            }
            if (!checkMin.equals(min) || !checkMax.equals(max)) {
                // We could not remove some of them because they were not completely included in the area
                return null;
            }
        }

        for (BlockVolume check : checkedVolumes) {
            remove(check);
        }

        return new BlockVolume(this, min, max);
    }

    /** Called by {@link WorldNetworkAccessor#markBlockForUpdate(BlockPos)} to notify about a block change. */
    void blockChanged(BlockPos pos) {
        if (canTrack(pos)) {
            for (EnumFacing face : EnumFacing.values()) {
                BlockPos offset = pos.offset(face);
                if (getVolume(offset) != null) {
                    queuedForChecking.add(pos);
                    queuedForChecking.add(offset);
                    break;
                }
            }
        } else {
            BlockVolume volume = volumeMap.get(pos);
            if (volume != null) {
                remove(volume);
                for (EnumFacing face : EnumFacing.values()) {
                    queuedForChecking.add(pos.offset(face));
                }
            }

            BlockPath path = pathMap.get(pos);
            if (path != null) {
                remove(path);
            }
        }
    }

    @Override
    void handlePacket(PacketPathfinding packet) {
        // Not quite yet
    }

    public void enque(BlockPos pos) {
        queuedForChecking.add(pos);
    }

    @Override
    public void tick() {
        super.tick();
        hasChanged = false;
        worldChecks = 0;
        hasMadePath = false;
        int preSize = queuedForChecking.size();
        long start = System.currentTimeMillis();
        while (worldChecks < MAX_WORLD_CHECKS) {
            if (queuedForChecking.size() == 0) {
                break;
            }
            BlockPos pos = queuedForChecking.iterator().next();
            queuedForChecking.remove(pos);
            BlockVolume volume = getVolume(pos);
            if (volume == null) {
                // add(searchAround(pos));
            } else {
                for (BlockPos test : Utils.allInBoxIncludingCorners(volume.min, volume.max)) {
                    queuedForChecking.remove(test);
                }
                mergeAround(volume);
            }
            if (System.currentTimeMillis() - start > 2) {
                // Ok, this is taking a bit too long :/
                BCLog.logger.warn("Robotics Network calculations took too long! (>2ms)");
                break;
            }

        }
        if (queuedForChecking.size() == 0 && preSize > 1) {
            BCLog.logger.info("Cleared the robotics checking queue");
        }

        if (worldChecks > 10) {
            // BCLog.logger.info("Checked the world for air " + worldChecks + " times!");
        }
    }

    public boolean canMakePath() {
        return !hasMadePath;
    }

    public BlockPath makePath(BlockPos pointA, BlockPos pointB) {
        BlockVolume volumeA = createIfNonExistant(pointA);
        BlockVolume volumeB = createIfNonExistant(pointB);
        if (volumeA == null || volumeB == null) {
            return null;
        }
        if (hasMadePath) {
            return null;
        }
        hasMadePath = true;
        return null;
    }

    BlockVolume createIfNonExistant(BlockPos point) {
        BlockVolume volume = getVolume(point);
        if (volume != null) {
            return volume;
        }
        volume = searchAround(point);
        if (volume != null) {
            add(volume);
            volume = getVolume(point);
            return volume;
        }
        return null;
    }

    private BlockVolume searchAround(BlockPos pos) {
        if (!canVolumeUse(pos)) {
            return null;
        }

        int expansionsLeft = 20;
        List<EnumFacing> faces = Lists.newArrayList(EnumFacing.values());
        BlockPos min = pos;
        BlockPos max = pos;
        EnumFacing currentExpansion = faces.get(0);

        while (expansionsLeft > 0 && faces.size() > 0) {
            BlockPos testMin = min;
            BlockPos testMax = max;
            if (currentExpansion.getAxisDirection() == AxisDirection.POSITIVE) {
                testMax = max.offset(currentExpansion);
            } else {
                testMin = min.offset(currentExpansion);
            }
            // if (currentExpansion.getAxisDirection() == AxisDirection.POSITIVE) {
            // testMin = Utils.getMinForFace(currentExpansion, testMin, testMax);
            // testMax = testMax.offset(currentExpansion);
            // } else {
            // testMax = Utils.getMaxForFace(currentExpansion, testMin, testMax);
            // testMin = testMin.offset(currentExpansion);
            // }
            boolean can = true;
            Set<BlockPath> affectedPaths = Sets.newIdentityHashSet();
            for (BlockPos toTest : Utils.allInBoxIncludingCorners(testMin, testMax)) {
                if (!canVolumeUse(toTest)) {
                    can = false;
                    break;
                }
                affectedPaths.add(getPath(toTest));
            }
            affectedPaths.remove(null);
            if (can) {
                expansionsLeft--;
                if (currentExpansion.getAxisDirection() == AxisDirection.POSITIVE) {
                    max = max.offset(currentExpansion);
                } else {
                    min = min.offset(currentExpansion);
                }
                for (BlockPath path : affectedPaths) {
                    path.invalidate();
                }
            } else {
                faces.remove(currentExpansion);
            }

            if (faces.size() > 0) {
                currentExpansion = faces.get(new Random().nextInt(faces.size()));
            } else {
                break;
            }
        }

        if (min.equals(max)) {
            return null;
        }

        return new BlockVolume(this, min, max);
    }
}
