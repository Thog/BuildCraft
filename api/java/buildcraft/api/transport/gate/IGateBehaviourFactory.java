package buildcraft.api.transport.gate;

import buildcraft.api.gates.IGate;

public interface IGateBehaviourFactory {
    GateBehaviour createNew(IGate gate, GateDefinition definition);
}
