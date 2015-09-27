package buildcraft.transport.event;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeEventMovementEnter;

public class PipeEventMovementEnter extends PipeEventMovement implements IPipeEventMovementEnter {
    private final EnumFacing origin;

    public PipeEventMovementEnter(IPipe pipe, IPipeContentsEditable contents, EnumFacing origin) {
        super(pipe, contents);
        this.origin = origin;
    }

    @Override
    public EnumFacing getOrigin() {
        return origin;
    }
}
