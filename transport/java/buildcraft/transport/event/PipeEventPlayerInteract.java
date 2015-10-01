package buildcraft.transport.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeWire;
import buildcraft.api.transport.event.IPipeEventPlayerInteract;

public abstract class PipeEventPlayerInteract extends PipeEventPart implements IPipeEventPlayerInteract {
    private final EntityPlayer player;

    public static PipeEventPlayerInteract create(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire, EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held == null) {
            return new PipeEventPlayerActivate(pipe, side, part, wire, player);
        } else if (held.getItem() instanceof IToolWrench) {
            return new PipeEventPlayerWrench(pipe, side, part, wire, player, (IToolWrench) held.getItem());
        } else {
            return new PipeEventPlayerUseItem(pipe, side, part, wire, player, held);
        }
    }

    public PipeEventPlayerInteract(IPipe pipe, EnumFacing side, PipePart part, PipeWire wire, EntityPlayer player) {
        super(pipe, side, part, wire);
        this.player = player;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }
}
