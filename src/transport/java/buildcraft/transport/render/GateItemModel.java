package buildcraft.transport.render;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ItemLayerModel.BakedModel;

import buildcraft.api.gates.IGateExpansion;
import buildcraft.core.lib.render.BuildCraftBakedModel;
import buildcraft.transport.gates.GateDefinition.GateLogic;
import buildcraft.transport.gates.GateDefinition.GateMaterial;
import buildcraft.transport.item.ItemGate;

public class GateItemModel extends BuildCraftBakedModel implements ISmartItemModel {
    private static class GateInfo {
        final GateLogic logic;
        final GateMaterial material;
        final ImmutableSet<IGateExpansion> expansions;

        public GateInfo(GateLogic logic, GateMaterial material, ImmutableSet<IGateExpansion> expansions) {
            this.logic = logic;
            this.material = material;
            this.expansions = expansions;
        }

        public static GateInfo create(ItemStack stack) {
            GateLogic logic = ItemGate.getLogic(stack);
            GateMaterial material = ItemGate.getMaterial(stack);
            Set<IGateExpansion> expansions = ItemGate.getInstalledExpansions(stack);
            return new GateInfo(logic, material, ImmutableSet.copyOf(expansions));
        }

        // Eclipse-generated methods

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((expansions == null) ? 0 : expansions.hashCode());
            result = prime * result + ((logic == null) ? 0 : logic.hashCode());
            result = prime * result + ((material == null) ? 0 : material.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            GateInfo other = (GateInfo) obj;
            if (expansions == null) {
                if (other.expansions != null)
                    return false;
            } else if (!expansions.equals(other.expansions))
                return false;
            if (logic != other.logic)
                return false;
            if (material != other.material)
                return false;
            return true;
        }
    }

    private static Map<GateInfo, BakedModel> modelCache = Maps.newHashMap();

    protected GateItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format) {
        super(quads, particle, format, getItemTransforms());
    }

    @Override
    public BakedModel handleItemState(ItemStack stack) {
        return handle(stack);
    }

    public static BakedModel handle(ItemStack stack) {
        GateInfo gateInfo = GateInfo.create(stack);
        if (modelCache.containsKey(gateInfo)) {
            return modelCache.get(gateInfo);
        }

        List<BakedQuad> quads = Lists.newArrayList();

        // Bake the base gate
        TextureAtlasSprite sprite = gateInfo.logic.getIconItem();
        float[] uvs = getUVArray(sprite);

        bakeFace(quads, EnumFacing.UP, new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0, 0.5f), uvs);

        sprite = gateInfo.material.getIconItem();
        if (sprite != null) {
            uvs = getUVArray(sprite);
            bakeFace(quads, EnumFacing.UP, new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.01f, 0.5f), uvs);
            bakeFace(quads, EnumFacing.DOWN, new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.01f, 0.5f), uvs);
        }

        float offset = 0.02f;
        for (IGateExpansion expansion : gateInfo.expansions) {
            sprite = expansion.getOverlayItem();
            if (sprite != null) {
                uvs = getUVArray(sprite);
                bakeFace(quads, EnumFacing.UP, new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, offset, 0.5f), uvs);
                bakeFace(quads, EnumFacing.DOWN, new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, offset, 0.5f), uvs);
                offset += 0.01f;
            }
        }

        BakedModel model = new GateItemModel(ImmutableList.copyOf(quads), null, DefaultVertexFormats.BLOCK);
        modelCache.put(gateInfo, model);
        return model;
    }

    public static GateItemModel create() {
        return new GateItemModel(null, null, null);
    }
}
