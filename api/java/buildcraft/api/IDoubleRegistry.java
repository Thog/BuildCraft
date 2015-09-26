package buildcraft.api;

import java.util.Map.Entry;
import java.util.Set;

import buildcraft.api.transport.ObjectDefinition;

public interface IDoubleRegistry<T extends ObjectDefinition> extends ISimpleRegistry<T> {
    void registerDefinition(T definition);
    
    Set<Entry<String, T>> getDefinitions();
}
