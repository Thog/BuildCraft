package buildcraft.api.transport.event;

import buildcraft.api.transport.IPipe;

/** Fired when this connects to a block that IS a pipe. (Specifically something that implements IPipeTile and returns a
 * non-null IPipe object) */
public interface IPipeEventConnectPipe extends IPipeEventConnect {
    IPipe getConnectingPipe();
}
