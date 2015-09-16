package buildcraft.api.transport.event;

import buildcraft.api.transport.PipeWire;

public interface IPipeEventPart extends IPipeEventSided {
    public enum PipePart {
        /** The absolute centre part of the pipe. */
        PIPE_CENTER,
        /** One of the pipes connections to another block */
        CONNECTION,
        /** A pluggable object attached to the pipe (including gates, facades, filters lenses, etc) */
        PLUGGABLE,
        /** One of the 4 wires on a pipe (Could be any of the 4 colours) */
        WIRE
    }

    /** @return The part that this event is for. The use will differ on what this event is (use the javadoc on the class
     *         members for more information) */
    PipePart getPart();

    /** @return The wire type, or null if {@link #getPart()} does not return {@link PipePart#WIRE}. */
    PipeWire getWireType();
}
