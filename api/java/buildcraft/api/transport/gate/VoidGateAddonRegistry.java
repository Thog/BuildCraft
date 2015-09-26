package buildcraft.api.transport.gate;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

public enum VoidGateAddonRegistry implements IGateAddonRegistry {
    INSTANCE;

    @Override
    public void registerDefinition(GateAddonDefinition definition) {}

    @Override
    public Set<Entry<String, GateAddonDefinition>> getDefinitions() {
        return Collections.emptySet();
    }

    @Override
    public GateAddonDefinition getDefinition(String uniqueTag) {
        return null;
    }

    @Override
    public String getTag(GateAddonDefinition definition) {
        return null;
    }
}
