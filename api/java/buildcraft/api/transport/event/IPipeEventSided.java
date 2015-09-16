package buildcraft.api.transport.event;

import net.minecraft.util.EnumFacing;

/** Represents an event that happened on a particular side of the pipe. */
public interface IPipeEventSided extends IPipeEvent {
    /** @return The side that something happened on. Note that this may be null, depending on the event */
    EnumFacing getSide();
}
