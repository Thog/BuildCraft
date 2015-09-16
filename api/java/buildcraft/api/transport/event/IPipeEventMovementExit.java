package buildcraft.api.transport.event;

import net.minecraft.util.EnumFacing;

public interface IPipeEventMovementExit extends IPipeEventMovement {
    EnumFacing getDestination();
}
