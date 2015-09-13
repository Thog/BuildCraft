package buildcraft.transport.pipes;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;

import buildcraft.api.transport.IPipeRegistry;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.transport.item.ItemPipe;

public class PipeRegistry implements IPipeRegistry {
    private final Map<String, Pair<PipeDefinition, Item>> pipeMap = Maps.newHashMap();
    private final BiMap<PipeDefinition, Item> definitionItemMap = HashBiMap.create();

    @Override
    public Item registerPipeDefinition(PipeDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Tried to register with a null pipe definition!");
        }
        Item item = new ItemPipe(definition);
        item.setUnlocalizedName("pipe_" + definition.modUniqueTag);
        pipeMap.put(definition.globalUniqueTag, Pair.of(definition, item));
        definitionItemMap.put(definition, item);
        return item;
    }

    @Override
    public Set<Entry<String, Pair<PipeDefinition, Item>>> getPipeDefinitions() {
        return Collections.unmodifiableSet(pipeMap.entrySet());
    }

    @Override
    public PipeDefinition getDefinition(String uniqueTag) {
        Pair<PipeDefinition, Item> pair = pipeMap.get(uniqueTag);
        if (pair == null) {
            return null;
        }
        return pair.getLeft();
    }

    @Override
    public Item getItem(PipeDefinition definition) {
        return definitionItemMap.get(definition);
    }

    @Override
    public PipeDefinition getDefinition(Item item) {
        return definitionItemMap.inverse().get(item);
    }

    @Override
    public String getUniqueTag(Item item) {
        PipeDefinition definition = getDefinition(item);
        if (definition == null) {
            return null;
        }
        return definition.globalUniqueTag;
    }
}
