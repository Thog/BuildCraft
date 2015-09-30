package buildcraft.api.transport;

public enum TransportFactoryStructure implements ITransportFactory {
    INSTANCE;

    @Override
    public PipeTransport create(IPipeTile tile) {
        return new PipeTransportStructure(tile);
    }
}
