package buildcraft.transport.internal.pipes;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEvent;

abstract class PipeEvent implements IPipeEvent {
    private final IPipe pipe;

    PipeEvent(IPipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public IPipe getPipe() {
        return pipe;
    }
}
