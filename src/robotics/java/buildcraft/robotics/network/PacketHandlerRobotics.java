package buildcraft.robotics.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import buildcraft.core.lib.network.Packet;
import buildcraft.core.lib.network.PacketHandler;
import buildcraft.core.network.PacketIds;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.robotics.pathfinding.PacketPathfinding;
import buildcraft.robotics.pathfinding.WorldNetworkManager;

import io.netty.channel.ChannelHandlerContext;

public class PacketHandlerRobotics extends PacketHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        super.channelRead0(ctx, packet);

        if (packet.getID() == PacketIds.PATHFINDING) {
            PacketPathfinding pathFinder = (PacketPathfinding) packet;

            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = CoreProxy.proxy.getPlayerFromNetHandler(netHandler);

            WorldNetworkManager.getForWorld(player.worldObj).addPacket(pathFinder);
        }
    }
}
