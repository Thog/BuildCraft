package buildcraft.api.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** Fired whenever a pipe attempts to connect or disconnect to another tile. */
public interface IPipeEventConnection extends IPipeEvent {
    /** @return The side that a connection change is being attempted at */
    EnumFacing getConnectingSide();

    /** @return The tile entity that is being connected to or disconnected from */
    TileEntity getConnectingTile();
}
