package buildcraft.transport.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeWire;
import buildcraft.api.transport.event.IPipeEventPlayerWrench;

class PipeEventPlayerWrench extends PipeEventPlayerInteract implements IPipeEventPlayerWrench {
    private final IToolWrench tool;

    PipeEventPlayerWrench(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire, EntityPlayer player, IToolWrench tool) {
        super(pipe, side, part, wire, player);
        this.tool = tool;
    }

    @Override
    public IToolWrench getWrench() {
        return tool;
    }
}
