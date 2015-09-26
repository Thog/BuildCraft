package buildcraft.api.transport.gate;

import buildcraft.api.gates.IGate;

public enum VoidGateBehaviourFactory implements IGateBehaviourFactory {
    INSTANCE;

    @Override
    public GateBehaviour createNew(IGate gate, GateDefinition definition) {
        return null;
    }
}
