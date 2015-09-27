package buildcraft.api.transport;

import buildcraft.transport.PipeTransport;

public interface IPipeType {
    PipeTransport createTransport();
}
