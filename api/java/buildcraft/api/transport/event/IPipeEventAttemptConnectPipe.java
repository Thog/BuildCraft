package buildcraft.api.transport.event;

import buildcraft.api.transport.IPipe;

/** Fired whenever a pipe attempts to connect to this pipe. */
public interface IPipeEventAttemptConnectPipe extends IPipeEventAttemptConnect {
    IPipe getConnectingPipe();
}
