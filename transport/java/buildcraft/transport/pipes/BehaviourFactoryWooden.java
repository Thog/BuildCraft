package buildcraft.transport.pipes;

import buildcraft.api.transport.IPipeBehaviourFactory;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;

public class BehaviourFactoryWooden implements IPipeBehaviourFactory {
    private PipeDefinition definition;

    public void setDefinition(PipeDefinition definition) {
        if (this.definition == null) {
            this.definition = definition;
        }
    }

    @Override
    public PipeBehaviour createNew(IPipeTile pipe) {
        return new BehaviourWood(definition, pipe);
    }
}
