package buildcraft.api.transport.event;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

// TODO: Add Proper JavaDoc and implement the ENTIRE SYSTEM DAMMIT!
public abstract class PipeEventMovement extends PipeEvent {
    public static class Enter extends PipeEventMovement {
        public final EnumFacing from;

        public Enter(double speed, PipeContents contents, EnumFacing from) {
            super(speed, contents);
            this.from = from;
        }
    }

    public static class ReachCenter extends PipeEventMovement {
        public final EnumFacing from;
        public final ImmutableMap<EnumFacing, TileEntity> potentialDestinations;
        public EnumFacing destination;

        public ReachCenter(double speed, PipeContents contents, EnumFacing from, Map<EnumFacing, TileEntity> potentialDestinations,
                EnumFacing destination) {
            super(speed, contents);
            this.from = from;
            this.potentialDestinations = ImmutableMap.copyOf(potentialDestinations);
            this.destination = destination;
        }
    }

    public static class Exit extends PipeEventMovement {
        public final EnumFacing to;

        public Exit(double speed, PipeContents contents, EnumFacing to) {
            super(speed, contents);
            this.to = to;
        }
    }

    /** The current speed of an item/fluid/power */
    public final double currentSpeed;
    /** The new speed of that item/fluid/power */
    public double newSpeed;

    public final PipeContents contents;

    public PipeEventMovement(double currentSpeed, PipeContents contents) {
        this.currentSpeed = currentSpeed;
        this.contents = contents;
    }
}
