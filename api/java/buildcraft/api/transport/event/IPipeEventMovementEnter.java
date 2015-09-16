package buildcraft.api.transport.event;

import net.minecraft.util.EnumFacing;

public interface IPipeEventMovementEnter extends IPipeEventMovement {
    EnumFacing getOrigin();
}
