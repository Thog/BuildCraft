package buildcraft.api.mj;

import java.util.List;

import com.google.common.collect.Lists;

public enum EnumMjDeviceType {
    /** Something that gives out power to anything, but only accepts power from other engines. */
    ENGINE(1),
    /** Something that gives out power to transport, and only accepts power from transport. */
    STORAGE(2),
    /** Something that gives out power to anything, and accepts power from anything. */
    TRANSPORT(1),
    /** Something that gives out no power, but accepts power from transport and engines. */
    MACHINE(4);

    private final List<EnumMjDeviceType> to;
    private final double flowDivisor;

    static {
        ENGINE.to.add(ENGINE);
        ENGINE.to.add(TRANSPORT);
        ENGINE.to.add(MACHINE);

        STORAGE.to.add(TRANSPORT);

        TRANSPORT.to.add(STORAGE);
        TRANSPORT.to.add(TRANSPORT);
        TRANSPORT.to.add(MACHINE);
    }

    EnumMjDeviceType(double flowDivisor) {
        this.to = Lists.newArrayList();
        this.flowDivisor = flowDivisor;
    }

    public boolean givesPowerTo(EnumMjDeviceType type) {
        return type == this || to.contains(type);
    }

    public boolean acceptsPowerFrom(EnumMjDeviceType type) {
        return type.givesPowerTo(this);
    }

    public double getFlowDivisor() {
        return flowDivisor;
    }
}
