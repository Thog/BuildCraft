package buildcraft.transport.event;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeEventMovement;

public abstract class PipeEventMovement extends PipeEvent implements IPipeEventMovement {
    private final IPipeContentsEditable contents;

    public PipeEventMovement(IPipe pipe, IPipeContentsEditable contents) {
        super(pipe);
        this.contents = contents;
    }

    @Override
    public IPipeContentsEditable getContents() {
        return contents;
    }
}
