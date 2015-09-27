package buildcraft.transport.event;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEvent;

public abstract class PipeEvent implements IPipeEvent {
    private final IPipe pipe;

    public PipeEvent(IPipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public IPipe getPipe() {
        return pipe;
    }
}
