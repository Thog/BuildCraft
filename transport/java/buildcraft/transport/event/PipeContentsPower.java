package buildcraft.transport.event;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsPower;

public class PipeContentsPower implements IPipeContentsPower {
    public double power;

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
