/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.network;

import java.util.Queue;

import com.google.common.collect.Queues;

import net.minecraft.world.World;

import buildcraft.core.TickHandlerCore;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {
    private final Queue<Packet> packets = Queues.newConcurrentLinkedQueue();

    public PacketHandler() {
        TickHandlerCore.addPacketHandler(this);
    }

    public void tick(World world) {
        Packet packet = null;
        while ((packet = packets.poll()) != null) {
            packet.applyData(world);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        packets.add(packet);
    }
}
