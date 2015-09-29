/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.network;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.BCLog;
import buildcraft.core.lib.network.command.PacketCommand;
import buildcraft.core.proxy.CoreProxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<Packet> {
    private static boolean recordStats = false;

    public static ChannelHandler createChannelHandler() {
        return recordStats ? new ChannelHandlerStats() : new ChannelHandler();
    }

    private int maxDiscriminator;

    protected ChannelHandler() {
        // Packets common to buildcraft.core.network
        addDiscriminator(0, PacketTileUpdate.class);
        addDiscriminator(1, PacketTileState.class);
        addDiscriminator(2, PacketSlotChange.class);
        addDiscriminator(3, PacketGuiReturn.class);
        addDiscriminator(4, PacketGuiWidget.class);
        addDiscriminator(5, PacketUpdate.class);
        addDiscriminator(6, PacketCommand.class);
        addDiscriminator(7, PacketEntityUpdate.class);
        maxDiscriminator = 8;
    }

    public void registerPacketType(Class<? extends Packet> packetType) {
        addDiscriminator(maxDiscriminator++, packetType);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, Packet packet, ByteBuf data) throws Exception {
        INetHandler handler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        EntityPlayer player = CoreProxy.proxy.getPlayerFromNetHandler(handler);
        if (player == null) {
            INetHandler h1 = ctx.attr(NetworkRegistry.NET_HANDLER).get();
            if (h1 != null) {
                EntityPlayer p1 = CoreProxy.proxy.getPlayerFromNetHandler(h1);
                handler = h1;
                player = p1;
                BCLog.logger.warn("Had to get the player via the context rather than the channel!");
            }
        }
        if (player == null) {
            switch (FMLCommonHandler.instance().getEffectiveSide()) {
                case CLIENT: {
                    // ok, WTF?
                    player = getMinecraftPlayer();
                    BCLog.logger.warn("Had to get the player via MINECRAFT rather than the channel!");
                    break;
                }
                case SERVER: {
                    MinecraftServer server = MinecraftServer.getServer();
                    for (WorldServer world : server.worldServers) {
                        for (EntityPlayer p2 : (List<EntityPlayer>) world.playerEntities) {
                            player = p2;
                            BCLog.logger.warn("Had to manually search the server for the first available player! THIS IS BAD!!!");
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (player == null) {
            throw new Exception("The player was null! (Encode)");
        } else {
            packet.writeData(data, player);
        }
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getMinecraftPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, Packet packet) {
        INetHandler handler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        EntityPlayer player = CoreProxy.proxy.getPlayerFromNetHandler(handler);
        if (player != null) {
            if (data.readableBytes() > 0) {
                packet.readData(data, player);
            } else {
                BCLog.logger.warn("Recieved a packet with no message! (" + packet + ")");
            }
        } else {
            BCLog.logger.warn("The player was null! (Decode)");
        }
    }
}
