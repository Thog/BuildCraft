package buildcraft.transport;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import buildcraft.api.IDoubleRegistry;
import buildcraft.api.transport.gate.GateDefinition;

public enum GateRegistry implements IDoubleRegistry<GateDefinition> {
    INSTANCE;

    private final BiMap<String, GateDefinition> map = HashBiMap.create();

    @Override
    public void registerDefinition(GateDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Tried to register with a null pipe definition!");
        }
    }

    @Override
    public Set<Entry<String, GateDefinition>> getDefinitions() {
        return map.entrySet();
    }

    @Override
    public GateDefinition getDefinition(String uniqueTag) {
        return map.get(uniqueTag);
    }

    @Override
    public String getTag(GateDefinition definition) {
        return map.inverse().get(definition);
    }
}
