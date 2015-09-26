package buildcraft.api.transport.pluggable;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.Item;

public enum VoidPluggableRegistry implements IPluggableRegistry {
    INSTANCE;

    @Override
    public Set<Triple<String, Item, PluggableDefinition>> getDefinitions() {
        return Collections.emptySet();
    }

    @Override
    public Item getItem(PluggableDefinition definition) {
        return null;
    }

    @Override
    public PluggableDefinition getDefinition(Item item) {
        return null;
    }

    @Override
    public String getUniqueTag(Item item) {
        return null;
    }

    @Override
    public Item getItem(String tag) {
        return null;
    }

    @Override
    public PluggableDefinition getDefinition(String uniqueTag) {
        return null;
    }

    @Override
    public String getTag(PluggableDefinition definition) {
        return null;
    }

    @Override
    public void registerDefinition(PluggableDefinition definition, Item item) {}
}
