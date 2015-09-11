package buildcraft.api.transport;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Contains all of the definitions of a pipe. */
public final class PipeDefinition {
    /** A globally unique tag for the pipe */
    public final String globalUniqueTag;
    /** A mod unique tag for the pipe. WARNING: this wshould only be used to register other mod-unique things, such as
     * the pipe item. For general use, use {@link #globalUniqueTag} */
    public final String modUniqueTag;
    /** The number of sprites to register related to this definition. */
    public final int maxSprites;
    /** A string containing the location of the texture of a pipe. The final texture location will be
     * {@link #textureLocationStart} + {@link #spriteLocations} */
    public final String textureLocationStart;
    /** An array containing the end locations of each pipe texture. This array have a length equal to maxSprites. It is
     * up to each indervidual pipe to determine which sprite to use for locations though. By default all of them will be
     * initialised to {@link #textureLocationStart}+{@link #globalUniqueTag} */
    public final String[] spriteLocations;
    /** A factory the creates the behaviour of this definition. */
    public final IBehaviourFactory behaviourFactory;
    /** The type of this pipe. This determines how the pipe should be rendered and how the pipe should act. */
    public final EnumPipeType type;

    /** An array containing the actual sprites, where the positions are determined by {@link #spriteLocations} */
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite[] sprites;

    public final int itemSpriteIndex;

    public PipeDefinition(String tag, EnumPipeType type, int maxSprites, String textureStart, IBehaviourFactory behaviour) {
        this(tag, type, maxSprites, 0, textureStart, behaviour);
    }

    /** @param tag A MOD-unique tag for the pipe
     * @param type The type of pipe this is
     * @param maxSprites The maximum number of sprites
     * @param itemSpriteIndex
     * @param textureStart
     * @param factory */
    public PipeDefinition(String tag, EnumPipeType type, int maxSprites, int itemSpriteIndex, String textureStart, IBehaviourFactory factory) {
        this.globalUniqueTag = getCurrentMod() + ":" + tag;
        this.modUniqueTag = tag;
        this.type = type;
        this.maxSprites = maxSprites;
        this.itemSpriteIndex = itemSpriteIndex;
        this.textureLocationStart = textureStart.endsWith("/") ? textureStart : (textureStart + "/");
        this.spriteLocations = new String[maxSprites];
        for (int i = 0; i < spriteLocations.length; i++) {
            spriteLocations[i] = textureLocationStart + tag + factory.createNew().getIconSuffix(i);
        }
        this.behaviourFactory = factory;
    }

    private static String getCurrentMod() {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            throw new IllegalStateException("Pipes MUST be registered inside a mod");
        }
        return container.getModId();
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSprite(int index) {
        if (sprites == null || index < 0 || index >= sprites.length) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
        return sprites[index];
    }

    @SideOnly(Side.CLIENT)
    public void registerSprites(TextureMap map) {
        sprites = new TextureAtlasSprite[maxSprites];
        for (int i = 0; i < spriteLocations.length; i++) {
            String string = spriteLocations[i];
            ResourceLocation location = new ResourceLocation(string);
            sprites[i] = map.registerSprite(location);
        }
    }
}
