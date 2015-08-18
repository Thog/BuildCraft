package buildcraft.api.mj;

import java.util.List;

import com.google.common.collect.Lists;

public enum EnumMjDevice {
    /** Something that gives out power to anything, but only accepts power from other engines. */
    ENGINE(3),
    /** Something that gives out power to transport, and only accepts power from transport. */
    STORAGE(2),
    /** Something that gives out power to anything, and accepts power from anything. */
    TRANSPORT(2),
    /** Something that gives out no power, but accepts power from transport and engines. */
    MACHINE(1);

    private final List<EnumMjDevice> to;
    private final double suctionDivisor;

    static {
        ENGINE.to.add(ENGINE);
        ENGINE.to.add(TRANSPORT);
        ENGINE.to.add(MACHINE);

        STORAGE.to.add(TRANSPORT);

        TRANSPORT.to.add(STORAGE);
        TRANSPORT.to.add(TRANSPORT);
        TRANSPORT.to.add(MACHINE);
    }

    EnumMjDevice(double flowDivisor) {
        this.to = Lists.newArrayList();
        this.suctionDivisor = flowDivisor;
    }

    public boolean givesPowerTo(EnumMjDevice type) {
        return type == this || to.contains(type);
    }

    public boolean acceptsPowerFrom(EnumMjDevice type) {
        return type.givesPowerTo(this);
    }

    public double getSuctionDivisor() {
        return suctionDivisor;
    }
}
