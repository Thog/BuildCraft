package buildcraft.transport.event;

import net.minecraft.entity.item.EntityItem;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventDropItem;

public class PipeEventDropItem extends PipeEvent implements IPipeEventDropItem {
    private final EntityItem item;

    public PipeEventDropItem(IPipe pipe, EntityItem item) {
        super(pipe);
        this.item = item;
    }

    @Override
    public EntityItem getDroppedItem() {
        return item;
    }
}
