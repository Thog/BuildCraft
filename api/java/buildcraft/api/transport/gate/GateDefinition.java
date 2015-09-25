package buildcraft.api.transport.gate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.transport.ObjectDefinition;

public final class GateDefinition extends ObjectDefinition {
    public final String spriteLocation;
    public final IGateBehaviourFactory behaviourFactory;

    /** An array containing the actual sprite, where the position is determined by {@link #spriteLocation} */
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprites;

    public GateDefinition(String tag, String spriteLocation, IGateBehaviourFactory behaviour) {
        super(tag);
        this.spriteLocation = spriteLocation;
        this.behaviourFactory = behaviour;
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
