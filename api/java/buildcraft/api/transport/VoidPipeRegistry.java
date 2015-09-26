package buildcraft.api.transport;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.Item;

public enum VoidPipeRegistry implements IPipeRegistry {
    INSTANCE;

    @Override
    public Set<Triple<String, Item, PipeDefinition>> getDefinitions() {
        return Collections.emptySet();
    }

    @Override
    public Item getItem(PipeDefinition definition) {
        return null;
    }

    @Override
    public PipeDefinition getDefinition(Item item) {
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
    public PipeDefinition getDefinition(String uniqueTag) {
        return null;
    }

    @Override
    public String getTag(PipeDefinition definition) {
        return null;
    }

    @Override
    public Item registerDefinition(PipeDefinition definition) {
        return null;
    }
}
