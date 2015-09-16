package buildcraft.api.transport.event;

import java.util.Random;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Fired whenever a random display tick occours on this pipe. Will only be fired at the client. */
@SideOnly(Side.CLIENT)
public interface IPipeEventRandomDisplayTick extends IPipeEvent {
    Random getRandom();
}
