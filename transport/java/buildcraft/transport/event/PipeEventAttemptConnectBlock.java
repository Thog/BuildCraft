package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventAttemptConnectBlock;

class PipeEventAttemptConnectBlock extends PipeEventAttemptConnect implements IPipeEventAttemptConnectBlock {
    PipeEventAttemptConnectBlock(IPipe pipe, EnumFacing side, TileEntity connectingTile, boolean asked, boolean correctType) {
        super(pipe, side, connectingTile, asked, correctType);
    }
}
