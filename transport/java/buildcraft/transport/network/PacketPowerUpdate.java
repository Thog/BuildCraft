/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import buildcraft.core.lib.network.PacketCoordinates;
import buildcraft.core.network.PacketIds;

import io.netty.buffer.ByteBuf;

public class PacketPowerUpdate extends PacketCoordinates {

    public byte[] power;
    public byte[] flow;

    public PacketPowerUpdate() {}

    public PacketPowerUpdate(BlockPos pos) {
        super(PacketIds.PIPE_POWER, pos);
    }

    @Override
    public void readData(ByteBuf stream, World world, EntityPlayer player) {
        super.readData(stream, world, player);
        power = new byte[6];
        flow = new byte[6];
        for (int i = 0; i < 6; i++) {
            power[i] = stream.readByte();
            flow[i] = stream.readByte();
        }
    }

    @Override
    public void writeData(ByteBuf stream, World world, EntityPlayer player) {
        super.writeData(stream, world, player);
        for (int i = 0; i < 6; i++) {
            stream.writeByte(power[i]);
            stream.writeByte(flow[i]);
        }
    }

    @Override
    public void applyData(World world) {

    }
}
