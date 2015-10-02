package buildcraft.transport.event;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventTick;

public class PipeEventTick extends PipeEvent implements IPipeEventTick {
    public PipeEventTick(IPipe pipe) {
        super(pipe);
    }
}
