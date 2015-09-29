package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnect;

public class PipeEventConnect extends PipeEventConnection implements IPipeEventConnect {
    public PipeEventConnect(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe, side, tile);
    }
}
