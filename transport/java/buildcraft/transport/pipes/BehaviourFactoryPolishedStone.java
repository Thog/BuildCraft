package buildcraft.transport.pipes;

import buildcraft.api.transport.PipeBehaviour;

public class BehaviourFactoryPolishedStone extends BehaviourFactoryBasic {
    @Override
    public PipeBehaviour createNew() {
        return new BehaviourPolishedStone(definition, connectionList, blacklist);
    }
}
