package buildcraft.transport;

import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.Item;

import buildcraft.api.transport.pluggable.IPluggableRegistry;
import buildcraft.api.transport.pluggable.PluggableDefinition;
import buildcraft.core.lib.HashDefinitionMap;

public enum PluggableRegistry implements IPluggableRegistry {
    INSTANCE;

    private final HashDefinitionMap<Item, PluggableDefinition> triMap = HashDefinitionMap.create();

    @Override
    public void registerDefinition(PluggableDefinition definition, Item item) {
        if (definition == null || item == null) {
            throw new IllegalArgumentException("Cannot register a null pluggable or item!");
        }
        triMap.put(item, definition);
    }

    @Override
    public Set<Triple<String, Item, PluggableDefinition>> getDefinitions() {
        return triMap.getTripleSet();
    }

    @Override
    public PluggableDefinition getDefinition(String uniqueTag) {
        return triMap.getDefinition(uniqueTag);
    }

    @Override
    public String getTag(PluggableDefinition definition) {
        return triMap.getTag(definition);
    }

    @Override
    public Item getItem(PluggableDefinition definition) {
        return triMap.getItem(definition);
    }

    @Override
    public PluggableDefinition getDefinition(Item item) {
        return triMap.getDefinition(item);
    }

    @Override
    public String getUniqueTag(Item item) {
        return triMap.getTag(item);
    }

    @Override
    public Item getItem(String tag) {
        return triMap.getItem(tag);
    }
}
