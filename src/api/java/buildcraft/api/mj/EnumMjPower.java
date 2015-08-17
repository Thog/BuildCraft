package buildcraft.api.mj;

import java.util.List;

import com.google.common.collect.Lists;

public enum EnumMjPower {
    /** A special effectively null value, that indicates that even though it implements IMjExternalStorage, it does not
     * actually deal with power */
    NONE,
    /** Low power type that is used (for example) for extracting items out of a chest or crafting a single item over a
     * second. This will generally be 1/10ths of an MJ. This is down convertible from NORMAL, but not up convertible.
     * This generally must be directly connected to the target to be used without loss. */
    REDSTONE,
    /** Normal power type that is used (for example) for placing and breaking items in the world or moving itself about.
     * This will generally be convertible to and from the LASER type , and down convertible to the REDSTONE type. This
     * kind needs kinesis pipes to transfer without loss. */
    NORMAL,
    /** Higher power type that can travel through air without losing power, this will generally be in the 100's of MJ.
     * If you want to receive this you will need to implement IMjLaserTarget on your tile entity. */
    LASER;

    private final List<EnumMjPower> to;

    static {
        NORMAL.to.add(REDSTONE);
        NORMAL.to.add(LASER);

        LASER.to.add(NORMAL);
    }

    private EnumMjPower() {
        to = Lists.newArrayList();
    }

    public boolean canConvertTo(EnumMjPower type) {
        return type == this || to.contains(type);
    }

    public boolean canConvertFrom(EnumMjPower type) {
        return type.canConvertTo(this);
    }
}
