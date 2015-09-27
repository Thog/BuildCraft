package buildcraft.transport.event;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventPowered;

public class PipeEventPowered extends PipeEvent implements IPipeEventPowered {
    private EnumFacing side;
    private double mj;

    public PipeEventPowered(IPipe pipe, EnumFacing side, double mj) {
        super(pipe);
        this.side = side;
        this.mj = mj;
    }

    @Override
    public double getMj() {
        return mj;
    }

    @Override
    public double useMj(double mj, boolean require) {
        if (mj <= this.mj) {
            this.mj -= mj;
            return mj;
        }
        if (mj > this.mj && require) {
            return 0;
        }
        double used = this.mj;
        this.mj = 0;
        return used;
    }

    @Override
    public EnumFacing getSide() {
        return side;
    }
}
