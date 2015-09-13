package buildcraft.robotics.pathfinding;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.core.lib.network.Packet;
import buildcraft.core.lib.utils.NetworkUtils;
import buildcraft.core.network.PacketIds;

import io.netty.buffer.ByteBuf;

public class PacketPathfinding extends Packet {
    enum ChangeType {
        ADD,
        REMOVE,
        RESET
    }

    enum BlockType {
        VOLUME,
        PATH
    }

    ChangeType change;
    BlockType type;
    BlockPos start, end;
    List<EnumFacing> faces;

    @Override
    public int getID() {
        return PacketIds.PATHFINDING;
    }

    @Override
    public void readData(ByteBuf stream) {
        change = NetworkUtils.readEnum(stream, ChangeType.class);
        type = NetworkUtils.readEnum(stream, BlockType.class);
        switch (type) {
            case PATH: {
                start = NetworkUtils.readBlockPos(stream);
                short length = stream.readShort();
                faces = Lists.newArrayListWithCapacity(length);
                for (; length > 0; length--) {
                    faces.add(NetworkUtils.readEnum(stream, EnumFacing.class));
                }
                break;
            }
            case VOLUME: {
                start = NetworkUtils.readBlockPos(stream);
                end = NetworkUtils.readBlockPos(stream);
                break;
            }
        }
    }

    @Override
    public void writeData(ByteBuf stream) {
        NetworkUtils.writeEnum(stream, change);
        NetworkUtils.writeEnum(stream, type);
        switch (type) {
            case PATH: {
                NetworkUtils.writeBlockPos(stream, start);
                stream.writeShort(faces.size());
                for (EnumFacing face : faces) {
                    NetworkUtils.writeEnum(stream, face);
                }
                break;
            }
            case VOLUME: {
                NetworkUtils.writeBlockPos(stream, start);
                NetworkUtils.writeBlockPos(stream, end);
                break;
            }
        }
    }

    @Override
    public void applyData(World world) {
        WorldNetworkManager.getForWorld(world).handlePacket(this);
    }
}
