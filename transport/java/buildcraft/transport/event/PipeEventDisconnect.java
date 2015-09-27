package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventDisconnect;

public class PipeEventDisconnect extends PipeEventConnection implements IPipeEventDisconnect {
    public PipeEventDisconnect(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe, side, tile);
    }
}
