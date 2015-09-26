package buildcraft.transport;

import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.Item;

import buildcraft.api.transport.IPipeRegistry;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.core.lib.HashDefinitionMap;
import buildcraft.transport.item.ItemPipe;

public enum PipeRegistry implements IPipeRegistry {
    INSTANCE;

    private final HashDefinitionMap<Item, PipeDefinition> triMap = HashDefinitionMap.create();

    @Override
    public Item registerDefinition(PipeDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Tried to register with a null pipe definition!");
        }
        Item item = new ItemPipe(definition);
        item.setUnlocalizedName("pipe_" + definition.modUniqueTag);
        triMap.put(definition.globalUniqueTag, item, definition);
        return item;
    }

    @Override
    public Set<Triple<String, Item, PipeDefinition>> getDefinitions() {
        return triMap.getTripleSet();
    }

    @Override
    public PipeDefinition getDefinition(String uniqueTag) {
        return triMap.getDefinition(uniqueTag);
    }

    @Override
    public Item getItem(PipeDefinition definition) {
        return triMap.getItem(definition);
    }

    @Override
    public PipeDefinition getDefinition(Item item) {
        return triMap.getDefinition(item);
    }

    @Override
    public String getUniqueTag(Item item) {
        return triMap.getTag(item);
    }

    @Override
    public String getTag(PipeDefinition definition) {
        return triMap.getTag(definition);
    }

    @Override
    public Item getItem(String tag) {
        return triMap.getItem(tag);
    }
}
