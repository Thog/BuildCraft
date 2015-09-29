package buildcraft.api.transport;

import buildcraft.transport.PipeTransport;

public interface IPipeType {
    // TODO (PASS 0): Convert this to an interface inside the API
    PipeTransport createTransport();
}
