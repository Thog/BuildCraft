package buildcraft.core.guide;

import net.minecraft.block.state.IBlockState;

public class GuideRenderedBlock extends GuidePart {
    private final IBlockState state;

    public GuideRenderedBlock(IBlockState state) {
        this.state = state;
    }
}
