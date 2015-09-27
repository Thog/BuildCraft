package buildcraft.transport.event;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeProperty;
import buildcraft.api.transport.event.IPipeEventUpdateProperty;

public class PipeEventUpdateProperty<T> extends PipeEvent implements IPipeEventUpdateProperty<T> {
    private final PipeProperty<T> property;
    private T value;

    public PipeEventUpdateProperty(IPipe pipe, PipeProperty<T> property, T value) {
        super(pipe);
        this.property = property;
        this.value = value;
    }

    @Override
    public PipeProperty<T> getProperty() {
        return property;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
