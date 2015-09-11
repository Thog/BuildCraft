package buildcraft.api.transport.event;

/** This is fired whenever any redstone level power is inserted to the pipe- this is NOT an event specific for power
 * pipes. This is meant for extraction style pipes. such as the wooden or emerald extraction pipes. */
public class PipeEventPowered extends PipeEvent {
    public final double mj;
    public double used = 0;

    public PipeEventPowered(double mj) {
        this.mj = mj;
    }
}
