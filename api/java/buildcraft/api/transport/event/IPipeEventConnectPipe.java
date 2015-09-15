package buildcraft.api.transport.event;

import buildcraft.api.transport.IPipe;

public interface IPipeEventConnectPipe extends IPipeEventConnect {
    IPipe getConnectingPipe();
}
