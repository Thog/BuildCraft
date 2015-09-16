package buildcraft.api.transport.event;

import net.minecraft.entity.player.EntityPlayer;

/** Fired whenever a player right clicks the pipe. */
public interface IPipeEventPlayerInteract extends IPipeEventPart {
    /** @return The player who right clicked the pipe */
    EntityPlayer getPlayer();
}
