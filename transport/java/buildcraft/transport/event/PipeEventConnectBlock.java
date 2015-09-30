package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnectBlock;

class PipeEventConnectBlock extends PipeEventConnect implements IPipeEventConnectBlock {
    PipeEventConnectBlock(IPipe pipe, EnumFacing side, TileEntity tile) {
        super(pipe, side, tile);
    }
}
