package buildcraft.transport.event;

import java.util.Random;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventRandomDisplayTick;

public class PipeEventRandomDisplayTick extends PipeEvent implements IPipeEventRandomDisplayTick {
    private final Random random;

    public PipeEventRandomDisplayTick(IPipe pipe, Random random) {
        super(pipe);
        this.random = random;
    }

    @Override
    public Random getRandom() {
        return random;
    }
}
