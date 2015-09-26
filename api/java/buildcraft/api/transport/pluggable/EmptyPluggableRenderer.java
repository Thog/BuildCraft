package buildcraft.api.transport.pluggable;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.transport.IPipe;

public enum EmptyPluggableRenderer implements IPluggableDynamicRenderer,IPluggableStaticRenderer {
    INSTANCE;

    @Override
    @SideOnly(Side.CLIENT)
    public List<BakedQuad> renderStaticPluggable(IPipeRenderState render, IPipePluggableState pluggableState, IPipe pipe, PipePluggable pluggable,
            EnumFacing face) {
        return Collections.emptyList();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamicPluggable(IPipe pipe, EnumFacing side, PipePluggable pipePluggable, double x, double y, double z) {}
}
