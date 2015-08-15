package buildcraft.api.mj;

import java.util.List;

import com.google.common.collect.Lists;

public enum EnumMjPowerType {
    /** Low power type that is used (for example) for extracting items out of a chest or crafting a single item over a
     * second. This will generally be 1/100ths of an MJ. This is not convertible between types */
    REDSTONE,
    /** Normal power type that is used (for example) for placing and breaking items in the world or moving itself about.
     * This will generally be convertible to and from the LASER type , and down convertible to the REDSTONE type */
    NORMAL,
    /** Higher power type that can travel through air without losing power, this will generally be in the 100's of
     * MJ. */
    LASER;

    private final List<EnumMjPowerType> from, to;

    static {
        REDSTONE.from.add(NORMAL);

        NORMAL.from.add(LASER);
        NORMAL.from.add(LASER);
        NORMAL.to.add(REDSTONE);

        LASER.from.add(NORMAL);
        LASER.to.add(NORMAL);
    }

    private EnumMjPowerType() {
        from = Lists.newArrayList();
        to = Lists.newArrayList();
    }

    public boolean canConvertFrom(EnumMjPowerType type) {
        return from.contains(type) || type == this;
    }

    public boolean canConvertTo(EnumMjPowerType type) {
        return to.contains(type) || type == this;
    }
}
