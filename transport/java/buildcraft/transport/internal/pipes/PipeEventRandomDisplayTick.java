package buildcraft.transport.internal.pipes;

import java.util.Random;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventRandomDisplayTick;

@SideOnly(Side.CLIENT)
public class PipeEventRandomDisplayTick extends PipeEvent implements IPipeEventRandomDisplayTick {
    private final Random random;

    PipeEventRandomDisplayTick(IPipe pipe, Random random) {
        super(pipe);
        this.random = random;
    }

    @Override
    public Random getRandom() {
        return random;
    }
}
