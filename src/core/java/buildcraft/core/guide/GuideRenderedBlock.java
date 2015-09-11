package buildcraft.core.guide;

import net.minecraft.block.state.IBlockState;

public class GuideRenderedBlock extends GuidePart {
    private final IBlockState state;

    public GuideRenderedBlock(IBlockState state) {
        this.state = state;
    }

    @Override
    public void renderIntoArea(int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }
}
