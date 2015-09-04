package buildcraft.core.guide;

import net.minecraft.item.ItemStack;

public class GuideRenderedItem extends GuidePart {
    private final ItemStack stack;

    public GuideRenderedItem(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void renderIntoArea(int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        
    }
}
