/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.network;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import buildcraft.api.core.BCLog;
import buildcraft.core.TickHandlerCore;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;

/* Implementation note: while this does implement Sharable this isn't technically sharable because it has a packet map.
 * However its fine provided that you only pass a single instance of this to forge, as this handles the double sides by
 * itself. */
@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {
    private final Map<Side, Queue<Packet>> packetMap;

    public PacketHandler() {
        Map<Side, Queue<Packet>> map = Maps.newHashMap();
        map.put(Side.CLIENT, new ConcurrentLinkedQueue<Packet>());
        map.put(Side.SERVER, new ConcurrentLinkedQueue<Packet>());
        packetMap = ImmutableMap.copyOf(map);
        TickHandlerCore.addPacketHandler(this);
    }

    public void tick(World world) {
        Packet packet = null;
        Side side = world.isRemote ? Side.CLIENT : Side.SERVER;
        Queue<Packet> queue = packetMap.get(side);
        while ((packet = queue.poll()) != null) {
            packet.applyData(world);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Attribute<Side> attrSide = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE);
        Side side = attrSide.get();
        if (side != null) {
            packetMap.get(side).add(packet);
        } else {
            BCLog.logger.error("Found a message without a side! THIS IS VERY BAD, MAJOR ERRORS COULD OCCOUR!");
        }
    }
}
