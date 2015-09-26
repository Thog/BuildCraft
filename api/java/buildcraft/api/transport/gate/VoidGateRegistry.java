package buildcraft.api.transport.gate;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

public enum VoidGateRegistry implements IGateRegistry {
    INSTANCE;

    @Override
    public void registerDefinition(GateDefinition definition) {}

    @Override
    public Set<Entry<String, GateDefinition>> getDefinitions() {
        return Collections.emptySet();
    }

    @Override
    public GateDefinition getDefinition(String uniqueTag) {
        return null;
    }

    @Override
    public String getTag(GateDefinition definition) {
        return null;
    }
}
