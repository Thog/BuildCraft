package buildcraft.transport.pipes;

import net.minecraft.item.EnumDyeColor;

import buildcraft.api.transport.EnumPipeType;

/** This is only ever used for texturing, so it doesn't need to compute the hasCode or implement the equals method like
 * the superclass to work. */
public class PipeInfoColoured extends PipeInfo {
    public final EnumDyeColor colour;

    private PipeInfoColoured(EnumPipeType type, EnumPipeMaterial material, EnumDyeColor colour) {
        super(type, material);
        this.colour = colour;
    }

    @Override
    public String getSpriteLocation() {
        return super.getSpriteLocation() + lowerCase(colour);
    }
}
