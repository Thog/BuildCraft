package buildcraft.api.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** Fired whenever a pipe connects to or disconnects from another tile. */
public interface IPipeEventConnection extends IPipeEvent {
    /** @return The side that has made a connection */
    EnumFacing getConnectingSide();

    /** @return The tile entity that is has been connected to. */
    TileEntity getConnectingTile();
}
