package buildcraft.transport.pipes;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.EnumPipeType;

public class PipeInfoSided extends PipeInfo {
    public final EnumFacing face;

    private PipeInfoSided(EnumPipeType type, EnumPipeMaterial material, EnumFacing face) {
        super(type, material);
        this.face = face;
    }

    @Override
    public String getSpriteLocation() {
        return super.getSpriteLocation() + lowerCase(face);
    }
}
