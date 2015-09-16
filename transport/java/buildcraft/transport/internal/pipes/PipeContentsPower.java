package buildcraft.transport.internal.pipes;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsPower;

class PipeContentsPower implements IPipeContentsPower {
    double power;

    PipeContentsPower(double power) {
        this.power = power;
    }

    @Override
    public EnumPipeType getType() {
        return EnumPipeType.POWER;
    }

    @Override
    public double getPower() {
        return power;
    }
}
