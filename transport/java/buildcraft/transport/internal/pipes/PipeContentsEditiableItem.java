package buildcraft.transport.internal.pipes;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import buildcraft.api.transport.event.IPipeContentsEditable.IPipeContentsEditableItem;

class PipeContentsEditiableItem extends PipeContentsItem implements IPipeContentsEditableItem {
    PipeContentsEditiableItem(ItemStack stack, EnumDyeColor colour) {
        super(stack, colour);
    }

    @Override
    public void removeAll() {
        setStack(null);
    }

    @Override
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setColor(EnumDyeColor color) {
        this.colour = color;
    }
}
