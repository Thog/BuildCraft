package buildcraft.api.transport.gate;

import buildcraft.api.gates.IGate;

public abstract class GateBehaviour {
    public final IGate gate;

    public GateBehaviour(IGate gate) {
        this.gate = gate;
    }
}
