package buildcraft.transport;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.ITransportFactory;
import buildcraft.api.transport.PipeTransport;
import buildcraft.transport.internal.pipes.PipeTransportItems;

public enum TransportFactoryItem implements ITransportFactory {
    INSTANCE;

    @Override
    public PipeTransport create(IPipeTile tile) {
        return new PipeTransportItems(tile);
    }
}
