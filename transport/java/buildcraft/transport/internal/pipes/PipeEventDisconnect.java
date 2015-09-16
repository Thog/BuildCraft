package buildcraft.transport.internal.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventDisconnect;

class PipeEventDisconnect extends PipeEventConnection implements IPipeEventDisconnect {
    PipeEventDisconnect(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe, side, tile);
    }
}
