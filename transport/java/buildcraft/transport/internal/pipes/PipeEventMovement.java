package buildcraft.transport.internal.pipes;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeEventMovement;

abstract class PipeEventMovement extends PipeEvent implements IPipeEventMovement {
    final IPipeContentsEditable contents;

    PipeEventMovement(IPipe pipe, IPipeContentsEditable contents) {
        super(pipe);
        this.contents = contents;
    }

    @Override
    public IPipeContentsEditable getContents() {
        return contents;
    }
}
