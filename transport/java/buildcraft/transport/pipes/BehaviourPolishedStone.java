package buildcraft.transport.pipes;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import buildcraft.api.transport.PipeDefinition;
import buildcraft.api.transport.event.IPipeEventAttemptConnectBlock;
import buildcraft.transport.pipes.BehaviourFactoryBasic.EnumListStatus;

public class BehaviourPolishedStone extends BehaviourBasic {
    public BehaviourPolishedStone(PipeDefinition definition, ImmutableList<PipeDefinition> connectionList, EnumListStatus blacklist) {
        super(definition, connectionList, blacklist);
    }

    @Subscribe
    public void connectBlockEvent(IPipeEventAttemptConnectBlock connect) {
        connect.disallow();
    }
}
