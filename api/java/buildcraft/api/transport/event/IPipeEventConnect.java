package buildcraft.api.transport.event;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

import buildcraft.api.mj.IMjHandler;
import buildcraft.api.transport.IPipeConnection;

public interface IPipeEventConnect extends IPipeEventConnection {
    /** @return True if the tile has implemented {@link IPipeConnection} and returned
     *         {@link IPipeConnection.ConnectOverride#CONNECT} */
    boolean hasAskedForConnection();

    /** @return True if the tile is currently allowed to connect. */
    boolean isAllowed();

    /** @param allow The new allowing value to set. */
    void setAllowed(boolean allow);

    /** @return True if the following rules are true:
     *         <ul>
     *         <li>This pipe is an item pipe AND the tile implements {@link IInventory}</li>OR
     *         <li>This pipe is a fluid pipe AND the tile implements {@link IFluidHandler}</li>OR
     *         <li>This pipe is a power pipe AND the tile implements {@link IMjHandler}</li>
     *         </ul>
    */
    boolean isCorrectType();
}
