package buildcraft.transport.pipes;

import buildcraft.api.transport.IPipeBehaviourFactory;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;

public class BehaviourFactoryWooden implements IPipeBehaviourFactory {
    private PipeDefinition definition;

    public void setDefinition(PipeDefinition definition) {
        if (definition == null) {
            this.definition = definition;
        }
    }

    @Override
    public PipeBehaviour createNew() {
        return new PipeBehaviourWood(definition);
    }
}
