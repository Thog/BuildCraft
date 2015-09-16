package buildcraft.api.transport.event;

import net.minecraft.util.EnumFacing;

/** Note that this will never be fired for power pipes. (They don't have a centre). */
public interface IPipeEventMovementReachCenter extends IPipeEventMovement {
    EnumFacing getOrigin();

    EnumFacing getDestination();
}
