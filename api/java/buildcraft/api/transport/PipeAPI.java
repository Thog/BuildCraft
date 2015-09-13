package buildcraft.api.transport;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;

public class PipeAPI {
    /** The base pipe registry. Register all pipes into this, but only AFTER BuildCraft Transport has completed
     * pre-init. */
    public static IPipeRegistry registry = new IPipeRegistry() {
        @Override
        public Item registerPipeDefinition(PipeDefinition definition) {
            return null;
        }

        @Override
        public Set<Entry<String, Pair<PipeDefinition, Item>>> getPipeDefinitions() {
            return Collections.emptySet();
        }

        @Override
        public PipeDefinition getDefinition(String uniqueTag) {
            return null;
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
    };
}
