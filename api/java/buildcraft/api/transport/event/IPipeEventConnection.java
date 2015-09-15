package buildcraft.api.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IPipeEventConnection extends IPipeEvent {
    EnumFacing getConnectingSide();

    TileEntity getConnectingTile();
}
