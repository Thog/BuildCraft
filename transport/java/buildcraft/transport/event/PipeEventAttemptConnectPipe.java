package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventAttemptConnectPipe;

public class PipeEventAttemptConnectPipe extends PipeEventAttemptConnect implements IPipeEventAttemptConnectPipe {
    private final IPipe otherPipe;

    PipeEventAttemptConnectPipe(IPipe pipe, EnumFacing side, TileEntity connectingTile, boolean asked, boolean correctType, IPipe otherPipe) {
        super(pipe, side, connectingTile, asked, correctType);
        this.otherPipe = otherPipe;
    }

    @Override
    public IPipe getConnectingPipe() {
        return otherPipe;
    }
}
