package buildcraft.transport.render.tile;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import buildcraft.transport.gates.GatePluggable;

public class PipeRendererGates {
    public static List<BakedQuad> renderGateStatic(GatePluggable gate, EnumFacing face, double x, double y, double z) {
        List<BakedQuad> quads = Lists.newArrayList();
        // TODO (PASS 1): Fix static gate rendering!
        return quads;
    }
}
