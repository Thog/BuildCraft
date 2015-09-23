/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.network;

import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import buildcraft.core.lib.network.PacketCoordinates;
import buildcraft.core.lib.utils.BitSetUtils;
import buildcraft.core.lib.utils.NetworkUtils;
import buildcraft.core.network.PacketIds;
import buildcraft.transport.internal.pipes.PipeTransportFluids;
import buildcraft.transport.internal.pipes.TileGenericPipe;
import buildcraft.transport.utils.FluidRenderData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketFluidUpdate extends PacketCoordinates {
    public FluidRenderData renderCache = new FluidRenderData();
    public BitSet delta;
    /** indicies 0-5 are for the connections, indicies 6-8 are for the centre bit's 3 axis */
    public byte[] flow;

    private ByteBuf payloadData;

    public PacketFluidUpdate(BlockPos pos) {
        super(PacketIds.PIPE_LIQUID, pos);
    }

    public PacketFluidUpdate(BlockPos pos, boolean chunkPacket) {
        super(PacketIds.PIPE_LIQUID, pos);
        this.isChunkDataPacket = chunkPacket;
    }

    public PacketFluidUpdate() {}

    @Override
    public void readData(ByteBuf data, EntityPlayer player) {
        super.readData(data, player);

        int length = data.readInt();
        payloadData = data.readBytes(length);
    }

    @Override
    public void writeData(ByteBuf data, EntityPlayer player) {
        super.writeData(data, player);

        ByteBuf payloadData = Unpooled.buffer();

        NetworkUtils.writeByteArray(payloadData, flow);

        byte[] dBytes = BitSetUtils.toByteArray(delta, 1);
        // System.out.printf("write %d, %d, %d = %s, %s%n", posX, posY, posZ, Arrays.toString(dBytes), delta);
        payloadData.writeBytes(dBytes);

        if (delta.get(0)) {
            payloadData.writeShort(renderCache.fluidID);
            if (renderCache.fluidID != 0) {
                payloadData.writeInt(renderCache.color);
            }
        }

        for (int dir = 0; dir < 7; dir++) {
            if (delta.get(dir + 1)) {
                payloadData.writeByte(renderCache.amount[dir]);
            }
        }

        data.writeInt(payloadData.readableBytes());
        data.writeBytes(payloadData);
    }

    @Override
    public int getID() {
        return PacketIds.PIPE_LIQUID;
    }

    @Override
    public void applyData(World world) {
        flow = NetworkUtils.readByteArray(payloadData);

        TileEntity entity = world.getTileEntity(pos);
        if (!(entity instanceof TileGenericPipe)) {
            return;
        }

        TileGenericPipe pipe = (TileGenericPipe) entity;
        if (pipe.pipe == null) {
            return;
        }

        if (!(pipe.pipe.transport instanceof PipeTransportFluids)) {
            return;
        }

        PipeTransportFluids transLiq = (PipeTransportFluids) pipe.pipe.transport;

        transLiq.displayFlow = flow;

        renderCache = transLiq.renderCache;

        byte[] dBytes = new byte[1];
        payloadData.readBytes(dBytes);
        delta = BitSetUtils.fromByteArray(dBytes);

        if (delta.get(0)) {
            renderCache.fluidID = payloadData.readShort();
            renderCache.color = renderCache.fluidID != 0 ? payloadData.readInt() : 0xFFFFFF;
        }

        for (int dir = 0; dir < 7; dir++) {
            if (delta.get(dir + 1)) {
                renderCache.amount[dir] = Math.min(transLiq.getCapacity(), payloadData.readUnsignedByte());
            }
        }
    }
}
