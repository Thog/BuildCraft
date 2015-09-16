package buildcraft.api.transport.event;

/** Fired whenever a pipe is powered by redstone power. */
public interface IPipeEventPowered extends IPipeEventSided {
    /** @return The amount of Mj available to use */
    double getMj();

    /** @param mj The amount of Mj to try and use.
     * @param requireAll If true then refuse to take any power if this does not contain enough Mj
     * @return The mj that was taken from storage (and so will be taken from whatever inserted the power) */
    double useMj(double mj, boolean requireAll);
}
