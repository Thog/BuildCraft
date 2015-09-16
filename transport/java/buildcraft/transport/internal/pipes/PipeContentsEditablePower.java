package buildcraft.transport.internal.pipes;

import buildcraft.api.transport.event.IPipeContentsEditable.IPipeContentsEditablePower;

class PipeContentsEditablePower extends PipeContentsPower implements IPipeContentsEditablePower {
    PipeContentsEditablePower(double power) {
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
