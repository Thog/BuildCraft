package buildcraft.transport;

import java.util.List;

import javax.vecmath.Vector3f;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IFacadePluggable;
import buildcraft.api.transport.pluggable.IPipePluggableState;
import buildcraft.api.transport.pluggable.IPipePluggableStaticRenderer;
import buildcraft.api.transport.pluggable.IPipeRenderState;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.render.BuildCraftBakedModel;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.pipes.EnumPipeMaterial;

public final class FacadePluggableRenderer extends BuildCraftBakedModel implements IPipePluggableStaticRenderer {
    public static final IPipePluggableStaticRenderer INSTANCE = new FacadePluggableRenderer();

    private FacadePluggableRenderer() {
        super(null, null, null);// We only extend BuildCraftBakedModel to get the model functions
    }

    @Override
    public List<BakedQuad> renderStaticPluggable(IPipeRenderState render, IPipePluggableState pluggableState, IPipe pipe, PipePluggable pluggable,
            EnumFacing face) {
        List<BakedQuad> quads = Lists.newArrayList();
        IFacadePluggable facade = (IFacadePluggable) pluggable;

        // Use the particle texture for the block. Not ideal, but we have NO way of getting the actual
        // texture of the block without hackery...
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(facade.getCurrentState());

        // Render the actual facade
        Vec3 center = Utils.VEC_HALF.add(Utils.convert(face, 7 / 16d));
        Vec3 radius = Utils.VEC_HALF.subtract(Utils.convert(Utils.convertPositive(face), 14 / 32d));

        for (EnumFacing renderFace : EnumFacing.VALUES) {
            if (face.getAxis() != renderFace.getAxis()) {
                PipePluggable onRenderFace = pluggableState.getPluggable(renderFace);
                if (onRenderFace != null && onRenderFace instanceof IFacadePluggable) {
                    continue;
                }
            }

            double offset = -0.001 * (face.ordinal() + 1);
            offset += 1;

            Vector3f centerF = Utils.convertFloat(center);
            Vector3f radiusF;
            if (face.getAxis() == renderFace.getAxis()) {
                radiusF = Utils.convertFloat(radius);
            } else {
                radiusF = Utils.convertFloat(Utils.withValue(radius, renderFace.getAxis(), Utils.getValue(radius, renderFace.getAxis()) * offset));
            }

            int uSize = 16;
            int vSize = 16;

            float[] uvs = new float[4];
            uvs[U_MIN] = sprite.getMinU();
            uvs[U_MAX] = sprite.getInterpolatedU(uSize);
            uvs[V_MIN] = sprite.getMinV();
            uvs[V_MAX] = sprite.getInterpolatedV(vSize);

            bakeDoubleFace(quads, renderFace, centerF, radiusF, uvs);
        }

        if (facade.isHollow()) {
            return quads;
        }

        // Render the little box
        center = Utils.VEC_HALF.add(Utils.convert(face, 5 / 16d));
        radius = new Vec3(4 / 16d, 4 / 16d, 4 / 16d).subtract(Utils.convert(Utils.convertPositive(face), 3 / 16d));

        sprite = TransportItems.getPipe(EnumPipeType.STRUCTURE, EnumPipeMaterial.COBBLESTONE).getSprite(0);
        for (EnumFacing renderFace : EnumFacing.VALUES) {
            Vector3f centerF = Utils.convertFloat(center);
            Vector3f radiusF = Utils.convertFloat(radius);

            float[] uvs = new float[4];
            uvs[U_MIN] = sprite.getInterpolatedU(4);
            uvs[U_MAX] = sprite.getInterpolatedU(12);
            uvs[V_MIN] = sprite.getInterpolatedV(4);
            uvs[V_MAX] = sprite.getInterpolatedV(12);

            bakeDoubleFace(quads, renderFace, centerF, radiusF, uvs);
        }

        return quads;
    }
}
