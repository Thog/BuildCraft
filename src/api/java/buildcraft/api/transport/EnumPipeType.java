package buildcraft.api.transport;

public enum EnumPipeType {
    ITEM(true, true),
    POWER(true, false),
    FLUID(true, true),
    STRUCTURE(false, false);

    /** An array of pipe types that carry something */
    public static final EnumPipeType[] CONTENTS = { ITEM, POWER, FLUID };

    /** True for {@link #ITEM}, {@link #FLUID} and {link #POWER} */
    public final boolean carriesSomething;
    /** True for {@link #ITEM} and {@link #FLUID} */
    public final boolean carriesDifferentThings;

    private EnumPipeType(boolean carriesSomething, boolean carriesDifferentThings) {
        this.carriesSomething = carriesSomething;
        this.carriesDifferentThings = carriesDifferentThings;
    }
}
