package buildcraft.transport.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeWire;
import buildcraft.api.transport.event.IPipeEventPlayerUseItem;

class PipeEventPlayerUseItem extends PipeEventPlayerInteract implements IPipeEventPlayerUseItem {
    private final ItemStack stack;

    PipeEventPlayerUseItem(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire, EntityPlayer player, ItemStack stack) {
        super(pipe, side, part, wire, player);
        this.stack = stack;
    }

    @Override
    public ItemStack getItemStack() {
        return stack.copy();
    }
}
