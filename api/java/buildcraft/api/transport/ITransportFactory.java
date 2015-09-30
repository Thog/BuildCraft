package buildcraft.api.transport;

public interface ITransportFactory {
    PipeTransport create(IPipeTile tile);
}
