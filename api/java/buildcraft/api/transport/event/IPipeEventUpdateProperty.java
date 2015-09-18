package buildcraft.api.transport.event;

import buildcraft.api.transport.PipeProperty;

public interface IPipeEventUpdateProperty<T> extends IPipeEvent {
    PipeProperty<T> getProperty();

    T getValue();

    void setValue(T value);

    /** Call this to indicate that the return value of the property is not static- recommended if this value changes
     * every tick or so. Otherwise call IPipe.dirtyProperty whenever the value changes */
    void redirty();
}
