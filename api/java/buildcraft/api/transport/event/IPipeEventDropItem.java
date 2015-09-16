package buildcraft.api.transport.event;

import net.minecraft.entity.item.EntityItem;

public interface IPipeEventDropItem extends IPipeEvent {
    EntityItem getDroppedItem();
}
