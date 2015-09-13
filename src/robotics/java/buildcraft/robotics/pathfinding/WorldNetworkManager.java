package buildcraft.robotics.pathfinding;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.core.BCLog;
import buildcraft.core.lib.utils.Utils;

public abstract class WorldNetworkManager {

    // We need 2 separate maps because both are used if its a single player world (The integrated server)
    private static final Map<Integer, WorldNetworkManager> serverNetworkManagerMap = Maps.newHashMap();
    private static final Map<Integer, WorldNetworkManager> clientNetworkManagerMap = Maps.newHashMap();

    protected final World world;
    /** A quick access map of BlockPos -> Volume that can be used to detect which volume a robot is in. */
    protected final Map<BlockPos, BlockVolume> volumeMap = Maps.newHashMap();
    /** A quick access map of BlockPos -> Path that can be used to detect which path a robot is in. */
    protected final Map<BlockPos, BlockPath> pathMap = Maps.newHashMap();
    /** A slow access list of all the volumes that have been added. The volumeMap is built from this list. */
    protected final Set<BlockVolume> volumeSet = Sets.newIdentityHashSet();
    /** A slow access list of all the paths that have been added. The pathMap is built from this list. */
    protected final Set<BlockPath> pathSet = Sets.newIdentityHashSet();

    private volatile List<PacketPathfinding> packets = Lists.newArrayList();

    public static WorldNetworkManager getForWorld(World world) {
        if (world == null || world.provider == null) {
            BCLog.logger.warn("world = " + (world == null ? "null" : "A world"));
            if (world != null) {
                BCLog.logger.warn("world.provider = " + (world.provider == null ? "null" : "A world Provider"));
            }
            return null;
        }
        int dimId = world.provider.getDimensionId();
        boolean client = world.isRemote;
        Map<Integer, WorldNetworkManager> map = client ? clientNetworkManagerMap : serverNetworkManagerMap;
        if (!map.containsKey(dimId)) {
            map.put(dimId, client ? new ClientNetworkManager(world) : new ServerNetworkManager(world));
        }
        return map.get(dimId);
    }

    WorldNetworkManager(World world) {
        this.world = world;
    }

    void add(BlockVolume volume) {
        if (volume == null) {
            return;
        }
        volume = volume.joinWithManager(this);
        volumeSet.add(volume);
        for (BlockPos pos : Utils.allInBoxIncludingCorners(volume.min, volume.max)) {
            volumeMap.put(pos, volume);
        }
        volume.joinPaths();
        volume.joinAround();
    }

    void add(BlockPath path) {
        if (path == null) {
            return;
        }
        path = path.joinWithManager(this);
        pathSet.add(path);
        for (BlockPos pos : path.dependentBlocks) {
            pathMap.put(pos, path);
        }
        if (path.one != null) {
            path.one.specialistPaths.add(path);
        }
        if (path.two != null) {
            path.two.specialistPaths.add(path);
        }
    }

    public BlockVolume getVolume(BlockPos pos) {
        return volumeMap.get(pos);
    }

    public BlockPath getPath(BlockPos pos) {
        return pathMap.get(pos);
    }

    public boolean isTracked(BlockPos pos) {
        return getVolume(pos) != null || getPath(pos) != null;
    }

    abstract void remove(BlockPath path);

    abstract void remove(BlockVolume volume);

    abstract void handlePacket(PacketPathfinding packet);
}
