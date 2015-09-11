package buildcraft.transport.pipes;

import java.util.Locale;

import buildcraft.api.transport.EnumPipeType;

@Deprecated
public class PipeInfo {
    public final EnumPipeType type;
    public final EnumPipeMaterial material;
    private final int hash;

    public PipeInfo(EnumPipeType type, EnumPipeMaterial material) {
        this.type = type;
        this.material = material;
        // Auto-generated hash code.
        final int prime = 31;
        int result = 1;
        result = prime * result + ((material == null) ? 0 : material.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        hash = result;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // Eclipse auto-generated method
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PipeInfo other = (PipeInfo) obj;
        if (material != other.material)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public String getSpriteLocation() {
        return "buildcrafttransport:pipes/pipe" + lowerCase(type) + lowerCase(material);
    }

    /** Used to set the unlocalised name of an item */
    public String getUnlocalizedName() {
        return "buildcraftpipe" + lowerCase(type) + lowerCase(material);
    }

    protected String lowerCase(Enum<?> e) {
        if (e == null) {
            return "_null";
        }
        return "_" + e.name().toLowerCase(Locale.ROOT);
    }
}
