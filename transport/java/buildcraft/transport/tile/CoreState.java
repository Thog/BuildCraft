package buildcraft.transport.tile;

import net.minecraft.item.Item;

import buildcraft.api.core.ISerializable;
import buildcraft.api.transport.PipeAPI;
import buildcraft.api.transport.PipeDefinition;

import io.netty.buffer.ByteBuf;

public class CoreState implements ISerializable {
    int pipeId = -1;
    private PipeDefinition definition = null;

    @Override
    public void writeData(ByteBuf data) {
        data.writeInt(pipeId);
    }

    @Override
    public void readData(ByteBuf data) {
        pipeId = data.readInt();
        definition = PipeAPI.registry.getDefinition(Item.getItemById(pipeId));
    }

    public PipeDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(PipeDefinition definition) {
        this.definition = definition;
        Item item = PipeAPI.registry.getItem(definition);
        if (item == null) {
            pipeId = -1;
        } else {
            pipeId = Item.getIdFromItem(item);
        }
    }
}
