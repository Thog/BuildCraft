package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnection;

public abstract class PipeEventConnection extends PipeEvent implements IPipeEventConnection {
    private final EnumFacing side;
    private final TileEntity tile;

    public PipeEventConnection(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe);
        this.side = side;
        this.tile = tile;
    }

    @Override
    public EnumFacing getConnectingSide() {
        return side;
    }

    @Override
    public TileEntity getConnectingTile() {
        return tile;
    }
}
