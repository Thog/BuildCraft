package buildcraft.transport.internal.pipes;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeEventMovementExit;

class PipeEventMovementExit extends PipeEventMovement implements IPipeEventMovementExit {
    private final EnumFacing destination;

    PipeEventMovementExit(IPipe pipe, IPipeContentsEditable contents, EnumFacing destination) {
        super(pipe, contents);
        this.destination = destination;
    }

    @Override
    public EnumFacing getDestination() {
        return destination;
    }
}
