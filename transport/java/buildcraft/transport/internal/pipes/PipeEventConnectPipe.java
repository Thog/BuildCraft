package buildcraft.transport.internal.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnectPipe;

class PipeEventConnectPipe extends PipeEventConnect implements IPipeEventConnectPipe {
    private final IPipe connectingPipe;

    PipeEventConnectPipe(IPipe pipe, EnumFacing side, TileEntity tile, boolean askedForConnection, boolean isCorrectType, IPipe connectingPipe) {
        super(pipe, side, tile, askedForConnection, isCorrectType);
        this.connectingPipe = connectingPipe;
    }

    @Override
    public IPipe getConnectingPipe() {
        return connectingPipe;
    }
}
