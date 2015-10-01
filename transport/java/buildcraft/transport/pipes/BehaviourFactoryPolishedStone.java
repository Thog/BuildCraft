package buildcraft.transport.pipes;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;

public class BehaviourFactoryPolishedStone extends BehaviourFactoryBasic {
    @Override
    public PipeBehaviour createNew(IPipeTile pipe) {
        return new BehaviourPolishedStone(definition, pipe, connectionList, blacklist);
    }
}
