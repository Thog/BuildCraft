package buildcraft.transport.pipes;

import com.google.common.collect.ImmutableList;

import buildcraft.api.transport.IBehaviourFactory;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;

public class BehaviourFactoryBasic implements IBehaviourFactory {
    public static enum EnumListStatus {
        WHITELIST,
        BLACKLIST
    }

    protected PipeDefinition definition;
    protected ImmutableList<PipeDefinition> connectionList = ImmutableList.of();
    protected EnumListStatus blacklist = EnumListStatus.BLACKLIST;

    public void setDefinition(PipeDefinition definition, EnumListStatus blacklist, PipeDefinition... connectionList) {
        if (this.definition == null) {
            this.definition = definition;
            this.connectionList = ImmutableList.copyOf(connectionList);
            this.blacklist = blacklist;
        }
    }

    @Override
    public PipeBehaviour createNew() {
        return new BehaviourBasic(definition, connectionList, blacklist);
    }
}
