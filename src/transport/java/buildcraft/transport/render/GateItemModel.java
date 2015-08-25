package buildcraft.transport.render;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ISmartItemModel;

import buildcraft.core.lib.render.BuildCraftBakedModel;

public class GateItemModel extends BuildCraftBakedModel implements ISmartItemModel {
    protected GateItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format) {
        super(quads, particle, format);
    }

    @Override
    public GateItemModel handleItemState(ItemStack stack) {
        return handle(stack);
    }

    public static GateItemModel handle(ItemStack stack) {
        return new GateItemModel(ImmutableList.<BakedQuad> of(), null, DefaultVertexFormats.BLOCK);
    }

    public static GateItemModel create() {
        return new GateItemModel(null, null, null);
    }
}
