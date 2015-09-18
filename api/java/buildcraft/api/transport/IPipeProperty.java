package buildcraft.api.transport;

public interface IPipeProperty<T> {
    T getValue(IPipe pipe);

    boolean hasValue(IPipe pipe);
}
