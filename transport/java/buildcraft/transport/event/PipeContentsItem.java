package buildcraft.transport.event;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;

public class PipeContentsItem implements IPipeContentsItem {
    protected ItemStack stack;
    protected EnumDyeColor colour;

    public PipeContentsItem(ItemStack stack, EnumDyeColor colour) {
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
