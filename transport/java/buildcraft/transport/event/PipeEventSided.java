package buildcraft.transport.event;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventSided;

public abstract class PipeEventSided extends PipeEvent implements IPipeEventSided {
    private final EnumFacing side;

    public PipeEventSided(IPipe pipe, EnumFacing side) {
        super(pipe);
        this.side = side;
    }

    @Override
    public EnumFacing getSide() {
        return side;
    }
}
