/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import buildcraft.core.lib.network.Packet;
import buildcraft.core.lib.utils.NetworkUtils;
import buildcraft.core.network.PacketIds;
import buildcraft.transport.TravelingItem;

import io.netty.buffer.ByteBuf;

public class PacketPipeTransportItemStack extends Packet {

    private ItemStack stack;
    private int entityId;

    public PacketPipeTransportItemStack() {}

    public PacketPipeTransportItemStack(int entityId, ItemStack stack) {
        this.entityId = entityId;
        this.stack = stack;
    }

    @Override
    public void writeData(ByteBuf data, EntityPlayer player) {
        super.writeData(data, player);
        data.writeInt(entityId);
        NetworkUtils.writeStack(data, stack);
    }

    @Override
    public void readData(ByteBuf data, EntityPlayer player) {
        super.readData(data, player);
        this.entityId = data.readInt();
        stack = NetworkUtils.readStack(data);
        TravelingItem item = TravelingItem.clientCache.get(entityId);
        if (item != null) {
            item.setItemStack(stack);
        }
    }

    public int getEntityId() {
        return entityId;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public int getID() {
        return PacketIds.PIPE_ITEMSTACK;
    }

    @Override
    public void applyData(World world) {
        // TODO Auto-generated method stub
    }
}
