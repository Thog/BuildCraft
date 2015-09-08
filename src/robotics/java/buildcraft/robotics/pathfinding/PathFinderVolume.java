package buildcraft.robotics.pathfinding;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.BlockPos;

import buildcraft.core.lib.utils.Utils;

/** Searches BlockVolume's BlockArea's and BlockPath's for a path between volumes. Invokes PathFinderBlock to try and
 * narrow down some of the gaps. */
public class PathFinderVolume {
    /* The maximum number of volumes that can be created by the create path algorithm per tick. Larger values need more
     * processing time to complete (As volumes can be very varied in size) and if there are large numbers of robots this
     * number would be better of small. */
    // TODO: Perform runtime changing of this number if the tick rate slows down?
    public static int MAX_VOLUMES_CREATED = 100;
    /* Significantly larger than created, as searching is just a case of iterating through a small array list This also
     * gives robots a max range of this number * whatever the average size of volumes are. Most of the time though the
     * volumes will be above ground and large, so the real limit is most likely going to be the amount of power robots
     * can store. Not final so that this can be edited in a config file. */
    public static int MAX_VOLUMES_SEARCHED = 10000;
    /** The minimum distance at which point PathFinderBlocks will be started to finish off the route. */
    // TODO: Test this value thoroughly!
    static final int MIN_BLOCK_DISTANCE = 30;

    final BlockVolume volumeA, volumeB;
    final WorldNetworkManager network;

    /** All of the next nodes that can be searched */
    private Map<BlockVolume, Node> openMap = Maps.newIdentityHashMap();
    /** All of the nodes that have been visited. */
    private Map<BlockVolume, Node> closedMap = Maps.newIdentityHashMap();
    private int created = 0;
    private int searched = 0;

    public enum EnumPathState {
        /** Not done yet, call this again next tick */
        INCOMPLETE,
        /** A path has been found, the completedPath variable will not be null */
        COMPLETE,
        /** It has been found that it is impossible for a path to be created between the points A and B. This will also
         * happen if this reached the volume searched limit. */
        IMPOSSIBLE
    }

    public static class FindingResult {
        public final RobotPath completedPath;
        public final EnumPathState searchState;

        private FindingResult(RobotPath completedPath, EnumPathState searchState) {
            this.completedPath = completedPath;
            this.searchState = searchState;
        }
    }

    static class Node {
        final Node parent;
        double destinationCost;
        double totalWeight;
        /** Will be null if the represents a path, and vice versa */
        final BlockVolume volume;
        final BlockPath path;
        final BlockPos entryPoint;

        public Node(Node parent, BlockPos entryPoint, BlockVolume volume, BlockPath path, BlockVolume destination) {
            this.parent = parent;
            this.volume = volume;
            this.path = path;
            this.entryPoint = entryPoint;

            // Use the min. We care about speed more than absolute perfection here
            // TODO CHECK: Is it ok to use the min?
            BlockPos destinationPoint = destination.min;

            destinationCost = destinationPoint.distanceSq(entryPoint);
            if (parent == null) {
                totalWeight = 0;
            } else {
                totalWeight = parent.totalWeight + entryPoint.distanceSq(parent.entryPoint);
            }
        }
    }

    /** @param pointA
     * @param pointB
     * @return The completed path. If this is null it meant that a path could not be found. */
    public static FindingResult createPath(WorldNetworkManager manager, BlockPos from, BlockVolume pointA, BlockVolume pointB) {
        PathFinderVolume pathFinder = new PathFinderVolume(manager, from, pointA, pointB);
        return null;
    }

    private PathFinderVolume(WorldNetworkManager manager, BlockPos from, BlockVolume pointA, BlockVolume pointB) {
        network = manager;
        volumeA = pointA;
        volumeB = pointB;
        Node node = new Node(null, from, pointA, null, pointB);
        openMap.put(pointA, node);
        while (searched < MAX_VOLUMES_SEARCHED && created < MAX_VOLUMES_CREATED) {
            Node searched = searchAround();
            if (searched.volume == pointB) {

                break;
            }
        }
    }

    private Node searchAround() {
        Node lowestCost = null;
        for (Node node : openMap.values()) {
            if (lowestCost == null) {
                lowestCost = node;
                break;
            }
            if (node.destinationCost < lowestCost.destinationCost) {
                lowestCost = node;
            }
        }

        BlockVolume volume = lowestCost.volume;
        boolean lessened = false;
        for (BlockArea area : volume.linkedAreas) {
            BlockVolume linkedVolume = area.one == volume ? area.two : area.one;
            BlockPos closestInside = Utils.getClosestInside(lowestCost.entryPoint, linkedVolume.min, linkedVolume.max);
            Node node = new Node(lowestCost, closestInside, linkedVolume, null, volumeB);
            openMap.put(linkedVolume, node);
            if (lowestCost.destinationCost > node.destinationCost) {
                lessened = true;
            }
            searched++;
        }
        if (!lessened) {
            // OK, we failed to find a volume that had a lower cost than what we started with- try to create more
            // volumes to see if they will help

            // Pick the direction and try to see if we can expand the current volume at all
//            BlockPos direction = ;
        }

        return lowestCost;
    }
}
