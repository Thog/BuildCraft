package buildcraft.core;

public enum PowerMode {

    M2(2),
    M4(4),
    M8(8),
    M16(16),
    M32(32),
    M64(64),
    M128(128);
    public static final PowerMode[] VALUES = values();
    public final double maxPower;

    private PowerMode(int max) {
        this.maxPower = max;
    }

    public PowerMode getNext() {
        PowerMode next = VALUES[(ordinal() + 1) % VALUES.length];
        return next;
    }

    public PowerMode getPrevious() {
        PowerMode previous = VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
        return previous;
    }

    public static PowerMode fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return M128;
        }
        return VALUES[id];
    }
}
