package buildcraft.api.transport.event;

import buildcraft.api.tools.IToolWrench;

/** Fired whenever a player used a wrench on the pipe */
public interface IPipeEventPlayerWrench extends IPipeEventPlayerInteract {
    /** @return The wrench that was used to right click the pipe */
    IToolWrench getWrench();
}
