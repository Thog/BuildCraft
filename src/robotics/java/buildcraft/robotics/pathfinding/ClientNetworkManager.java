package buildcraft.robotics.pathfinding;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.core.lib.utils.Utils;
import buildcraft.robotics.pathfinding.BlockPath.PathDirection;
import buildcraft.robotics.pathfinding.PacketPathfinding.BlockType;
import buildcraft.robotics.pathfinding.PacketPathfinding.ChangeType;

public class ClientNetworkManager extends WorldNetworkManager {
    ClientNetworkManager(World world) {
        super(world);
    }

    @Override
    void remove(BlockVolume volume) {
        if (volume == null) {
            return;
        }
        volume.invalidate();
        if (volumeSet.remove(volume)) {
            for (BlockPos pos : Utils.allInBoxIncludingCorners(volume.min, volume.max)) {
                volumeMap.remove(pos);
            }
        }
    }

    @Override
    void remove(BlockPath path) {
        if (path == null) {
            return;
        }
        path.invalidate();
        if (pathSet.remove(path)) {
            for (BlockPos pos : path.dependentBlocks) {
                pathMap.remove(pos);
            }
        }
    }

    @Override
    void handlePacket(PacketPathfinding packet) {
        if (packet.type == BlockType.VOLUME) {
            if (packet.change == ChangeType.ADD) {
                add(new BlockVolume(this, Utils.min(packet.start, packet.end), Utils.max(packet.start, packet.end)));
            } else if (packet.change == ChangeType.REMOVE) {
                remove(getVolume(packet.start));
            } else if (packet.change == ChangeType.RESET) {
                volumeMap.clear();
                volumeSet.clear();
            }
        } else if (packet.type == BlockType.PATH) {
            if (packet.change == ChangeType.ADD) {
                BlockVolume one = getVolume(packet.start);
                BlockVolume two = getVolume(packet.end);
                Map<PathDirection, ImmutableList<EnumFacing>> map = Maps.newHashMap();
                map.put(PathDirection.START_TO_END, ImmutableList.copyOf(packet.faces));
                map.put(PathDirection.END_TO_START, ImmutableList.copyOf(BlockPath.reverse(packet.faces)));
                ImmutableMap<PathDirection, ImmutableList<EnumFacing>> immutableFaces = ImmutableMap.copyOf(map);

                BlockPath path = new BlockPath(this, one, two, packet.start, packet.end, immutableFaces, BlockPath.dependantBlocks(packet.start,
                        packet.faces));
                add(path);
            } else if (packet.change == ChangeType.REMOVE) {
                remove(getPath(packet.start));
            } else if (packet.change == ChangeType.RESET) {
                pathSet.clear();
                pathMap.clear();
            }
        }
    }
}
