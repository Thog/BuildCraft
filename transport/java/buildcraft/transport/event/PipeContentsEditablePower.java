package buildcraft.transport.event;

import buildcraft.api.transport.event.IPipeContentsEditable.IPipeContentsEditablePower;

public class PipeContentsEditablePower extends PipeContentsPower implements IPipeContentsEditablePower {
    public PipeContentsEditablePower(double power) {
        super(power);
    }

    @Override
    public void removeAll() {
        setPower(0);
    }

    @Override
    public void setPower(double mj) {
        power = mj;
    }
}
