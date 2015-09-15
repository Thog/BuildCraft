package buildcraft.transport.render;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableState;
import buildcraft.api.transport.pluggable.IPipePluggableStaticRenderer;
import buildcraft.api.transport.pluggable.IPipeRenderState;
import buildcraft.api.transport.pluggable.PipePluggable;

public final class LensPluggableRenderer implements IPipePluggableStaticRenderer {
    public static final IPipePluggableStaticRenderer INSTANCE = new LensPluggableRenderer();
    private static final float zFightOffset = 1 / 4096.0F;

    private LensPluggableRenderer() {

    }
    // TODO (PASS 0: Fix this!
    //
    // @Override
    // public void renderPluggable(RenderBlocks renderblocks, IPipe pipe, EnumFacing side, PipePluggable
    // pipePluggable,
    // ITextureStates blockStateMachine, int renderPass, BlockPos pos) {
    // float[][] zeroState = new float[3][2];
    //
    // // X START - END
    // zeroState[0][0] = 0.1875F;
    // zeroState[0][1] = 0.8125F;
    // // Y START - END
    // zeroState[1][0] = 0.000F;
    // zeroState[1][1] = 0.125F;
    // // Z START - END
    // zeroState[2][0] = 0.1875F;
    // zeroState[2][1] = 0.8125F;
    //
    // if (renderPass == 1) {
    // blockStateMachine.setRenderMask(1 << side.ordinal() | (1 << (side.ordinal() ^ 1)));
    //
    // for (int i = 0; i < 3; i++) {
    // zeroState[i][0] += zFightOffset;
    // zeroState[i][1] -= zFightOffset;
    // }
    // blockStateMachine.getTextureState().setToStack(
    // BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.PipeLensOverlay.ordinal()));
    // ((FakeBlock) blockStateMachine).setColor(ColorUtils.getRGBColor(15 - ((LensPluggable) pipePluggable).color));
    //
    // blockStateMachine.setRenderAllSides();
    // } else {
    // if (((LensPluggable) pipePluggable).isFilter) {
    // blockStateMachine.getTextureState().setToStack(
    // BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.PipeFilter.ordinal()));
    // } else {
    // blockStateMachine.getTextureState().setToStack(
    // BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.PipeLens.ordinal()));
    // }
    // }
    //
    // float[][] rotated = MatrixTranformations.deepClone(zeroState);
    // MatrixTranformations.transform(rotated, side);
    //
    // renderblocks.setRenderBounds(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1],
    // rotated[2][1]);
    // renderblocks.renderStandardBlock(blockStateMachine.getBlock(), pos);
    //
    // ((FakeBlock) blockStateMachine).setColor(0xFFFFFF);
    // }

    @Override
    public List<BakedQuad> renderStaticPluggable(IPipeRenderState render, IPipePluggableState pluggableState, IPipe pipe, PipePluggable pluggable,
            EnumFacing face) {
        return null;
    }
}