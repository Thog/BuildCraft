package buildcraft.transport.event;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeWire;
import buildcraft.api.transport.event.IPipeEventPart;

public abstract class PipeEventPart extends PipeEventSided implements IPipeEventPart {
    private final PipePart part;
    private final PipeWire wire;

    public PipeEventPart(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire) {
        super(pipe, side);
        this.part = part;
        this.wire = wire;
    }

    @Override
    public PipePart getPart() {
        return part;
    }

    @Override
    public PipeWire getWireType() {
        return wire;
    }
}
