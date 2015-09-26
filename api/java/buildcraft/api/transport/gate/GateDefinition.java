package buildcraft.api.transport.gate;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.transport.ObjectDefinition;

public final class GateDefinition extends ObjectDefinition {
    public final String spriteLocation;
    public final IGateBehaviourFactory behaviourFactory;
    /** A list of any possible items that are required to make this gate. */
    public final ImmutableList<ItemStack> recipeItems;

    /** The actual sprite, where the position is determined by {@link #spriteLocation} */
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprites;

    public GateDefinition(String tag, String spriteLocation, IGateBehaviourFactory behaviour, List<ItemStack> possibleRecipeItems) {
        super(tag);
        this.spriteLocation = spriteLocation;
        this.behaviourFactory = behaviour;
        this.recipeItems = ImmutableList.copyOf(possibleRecipeItems);
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSprite() {
        if (sprites == null) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
        return sprites;
    }

    @SideOnly(Side.CLIENT)
    public void registerSprites(TextureMap map) {
        sprites = map.registerSprite(new ResourceLocation(spriteLocation));
    }
}
