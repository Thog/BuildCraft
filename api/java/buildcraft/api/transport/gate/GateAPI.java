package buildcraft.api.transport.gate;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;

public class GateAPI {
    public static IGateRegistry registry;

    public static IGateBehaviourFactory AND_FACTORY;
    public static IGateBehaviourFactory OR_FACTORY;

    static {
        registry = new IGateRegistry() {
            @Override
            public Item registerDefinition(GateDefinition definition) {
                return null;
            }

            @Override
            public String getUniqueTag(Item item) {
                return null;
            }

            @Override
            public Item getItem(GateDefinition definition) {
                return null;
            }

            @Override
            public Set<Entry<String, Pair<GateDefinition, Item>>> getDefinitions() {
                return Collections.emptySet();
            }

            @Override
            public GateDefinition getDefinition(Item item) {
                return null;
            }

            @Override
            public GateDefinition getDefinition(String uniqueTag) {
                return null;
            }
        };
    }
}
