package buildcraft.api.transport.event;

import net.minecraft.util.EnumFacing;

// TODO: Add Proper JavaDoc and implement the ENTIRE SYSTEM DAMMIT!
public abstract class PipeEventMovement extends PipeEvent {
    public static class Enter extends PipeEventMovement {
        public final EnumFacing from;

        public Enter(float speed, PipeContents contents, EnumFacing from) {
            super(speed, contents);
            this.from = from;
        }
    }

    public static class ReachCenter extends PipeEventMovement {
        public final EnumFacing from, to;

        public ReachCenter(float speed, PipeContents contents, EnumFacing from, EnumFacing to) {
            super(speed, contents);
            this.from = from;
            this.to = to;
        }
    }

    public static class Exit extends PipeEventMovement {
        public final EnumFacing to;
        public boolean handled = false;

        public Exit(float speed, PipeContents contents, EnumFacing to) {
            super(speed, contents);
            this.to = to;
        }
    }

    /** The current speed of an item/fluid/power */
    public final float currentSpeed;
    /** The new speed of that item/fluid/power */
    private float newSpeed = -1;

    public final PipeContents contents;

    public PipeEventMovement(float currentSpeed, PipeContents contents) {
        this.currentSpeed = currentSpeed;
        this.contents = contents;
    }

    public boolean hasSetNewSpeed() {
        return newSpeed > 0;
    }

    public float getSpeed() {
        return newSpeed < 0 ? currentSpeed : newSpeed;
    }

    public void setNewSpeed(float newSpeed) {
        this.newSpeed = newSpeed;
    }
}
