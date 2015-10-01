package buildcraft.api.transport;

public interface IPipeBehaviourFactory {
    PipeBehaviour createNew(IPipeTile pipe);
}
