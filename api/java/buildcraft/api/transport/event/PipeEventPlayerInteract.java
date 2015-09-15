package buildcraft.api.transport.event;

import net.minecraft.entity.player.EntityPlayer;

public abstract class PipeEventPlayerInteract extends PipeEvent {
    public static class RightClick extends PipeEventPlayerInteract {
        public RightClick(EntityPlayer player) {
            super(player);
        }
    }

    /** Fired whenever a player right clicks with a wrench. */
    public static class WrenchUsed extends RightClick {
        public WrenchUsed(EntityPlayer player) {
            super(player);
        }
    }

    public static class LeftClick extends PipeEventPlayerInteract {
        public LeftClick(EntityPlayer player) {
            super(player);
        }
    }

    /** Called every tick whenever a player is touching a pipe */
    public static class Touch extends PipeEventPlayerInteract {
        public Touch(EntityPlayer player) {
            super(player);
        }
    }

    public final EntityPlayer player;

    public PipeEventPlayerInteract(EntityPlayer player) {
        this.player = player;
    }
}
