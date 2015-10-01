package buildcraft.transport.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeWire;
import buildcraft.api.transport.event.IPipeEventPlayerActivate;

class PipeEventPlayerActivate extends PipeEventPlayerInteract implements IPipeEventPlayerActivate {
    PipeEventPlayerActivate(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire, EntityPlayer player) {
        super(pipe, side, part, wire, player);
    }
}
