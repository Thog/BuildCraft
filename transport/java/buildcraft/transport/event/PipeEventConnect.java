package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.event.IPipeEventConnect;

public abstract class PipeEventConnect extends PipeEventConnection implements IPipeEventConnect {
    public PipeEventConnect(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe, side, tile);
    }

    public static PipeEventConnect create(IPipe pipe, EnumFacing side, TileEntity with) {
        if (with instanceof IPipeTile) {
            return new PipeEventConnectPipe(pipe, side, with, ((IPipeTile) with).getPipe());
        } else {
            return new PipeEventConnectBlock(pipe, side, with);
        }
    }
}
