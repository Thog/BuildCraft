package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnectPipe;

class PipeEventConnectPipe extends PipeEventConnect implements IPipeEventConnectPipe {
    private final IPipe connectingPipe;

    PipeEventConnectPipe(IPipe pipe, EnumFacing side, TileEntity tile, IPipe connectingPipe) {
        super(pipe, side, tile);
        this.connectingPipe = connectingPipe;
    }

    @Override
    public IPipe getConnectingPipe() {
        return connectingPipe;
    }
}
