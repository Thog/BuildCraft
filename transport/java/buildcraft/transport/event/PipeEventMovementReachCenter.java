package buildcraft.transport.event;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeEventMovementReachCenter;

public class PipeEventMovementReachCenter extends PipeEventMovement implements IPipeEventMovementReachCenter {
    private final EnumFacing origin, destination;

    public PipeEventMovementReachCenter(IPipe pipe, IPipeContentsEditable contents, EnumFacing origin, EnumFacing destination) {
        super(pipe, contents);
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public EnumFacing getOrigin() {
        return origin;
    }

    @Override
    public EnumFacing getDestination() {
        return destination;
    }
}
