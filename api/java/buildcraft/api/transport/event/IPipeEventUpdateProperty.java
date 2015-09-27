package buildcraft.api.transport.event;

import buildcraft.api.transport.PipeProperty;

public interface IPipeEventUpdateProperty<T> extends IPipeEvent {
    PipeProperty<T> getProperty();

    T getValue();

    void setValue(T value);
}
