package buildcraft.api.transport.event;

import java.util.Random;

/** Fired whenever a random display tick occours on this pipe. Will only be fired at the client. */
public interface IPipeEventRandomDisplayTick extends IPipeEvent {
    Random getRandom();
}
