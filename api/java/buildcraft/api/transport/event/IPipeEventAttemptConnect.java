package buildcraft.api.transport.event;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

import buildcraft.api.mj.IMjHandler;
import buildcraft.api.transport.IPipeConnection;

/** Fired to check if this can connect to a different block. This may be fired at any time, even if nothing has actually
 * changed in the world about this connection. (So DO NOT update your state based on this). Note that if both
 * isCorrectType and hasAskedForConnection are false, the connection will always be disallowed. */
public interface IPipeEventAttemptConnect extends IPipeEvent {
    /** @return The side that a connection change is being attempted at */
    EnumFacing getConnectingSide();

    /** @return The tile entity that is being connected to or disconnected from */
    TileEntity getConnectingTile();

    /** @return True if the tile has implemented {@link IPipeConnection} and returned
     *         {@link IPipeConnection.ConnectOverride#CONNECT} */
    boolean hasAskedForConnection();

    /** @return True if the following rules are true:
     *         <ul>
     *         <li>This pipe is an item pipe AND the tile implements {@link IInventory}</li>OR
     *         <li>This pipe is a fluid pipe AND the tile implements {@link IFluidHandler}</li>OR
     *         <li>This pipe is a power pipe AND the tile implements {@link IMjHandler}</li>
     *         </ul>
    */
    boolean isCorrectType();

    /** @return True if the tile is currently allowed to connect. */
    boolean isAllowed();

    /** Call this to block the connection from being made. */
    void disallow();
}
