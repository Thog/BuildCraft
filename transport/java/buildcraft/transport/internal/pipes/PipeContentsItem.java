package buildcraft.transport.internal.pipes;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;

class PipeContentsItem implements IPipeContentsItem {
    ItemStack stack;
    EnumDyeColor colour;

    PipeContentsItem(ItemStack stack, EnumDyeColor colour) {
        this.stack = stack;
        this.colour = colour;
    }

    @Override
    public EnumPipeType getType() {
        return EnumPipeType.ITEM;
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.copyItemStack(stack);
    }

    @Override
    public EnumDyeColor getColor() {
        return colour;
    }

}
