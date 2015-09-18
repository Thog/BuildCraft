package buildcraft.api.enums;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumEngineType implements IStringSerializable {
    WOOD,
    STONE,
    IRON,
    CREATIVE;

    @Override
    public String getName() {
        return name();
    }

    public String getLowercaseName() {
        return getName().toLowerCase(Locale.ROOT);
    }
}
