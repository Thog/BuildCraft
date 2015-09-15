package buildcraft.api.transport.event;

import net.minecraft.entity.item.EntityItem;

public class PipeEventDropItem extends PipeEvent {
    /** The entity that will be created. If this is null or the item has a stack size less than or equal to zero, the
     * entity will not be created */
    public final EntityItem droppingItem;

    public PipeEventDropItem(EntityItem droppingItem) {
        this.droppingItem = droppingItem;
    }
}
