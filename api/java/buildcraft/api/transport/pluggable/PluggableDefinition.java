package buildcraft.api.transport.pluggable;

import buildcraft.api.transport.ObjectDefinition;

public final class PluggableDefinition extends ObjectDefinition {
    public final IPluggableBehaviourFactory factory;

    public PluggableDefinition(String tag, IPluggableBehaviourFactory factory) {
        super(tag);
        this.factory = factory;
    }
}
