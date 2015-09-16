package buildcraft.transport.pipes;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.api.transport.event.IPipeEventConnectPipe;
import buildcraft.transport.pipes.BehaviourFactoryBasic.EnumListStatus;

public class BehaviourBasic extends PipeBehaviour {
    public final ImmutableList<PipeDefinition> connectionList;
    public final EnumListStatus blacklist;

    public BehaviourBasic(PipeDefinition definition, ImmutableList<PipeDefinition> connectionList, EnumListStatus blacklist) {
        super(definition);
        this.connectionList = connectionList;
        this.blacklist = blacklist;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}

    @Override
    public int getIconIndex(EnumFacing side) {
        return 0;
    }

    @Subscribe
    public void pipeConnectEvent(IPipeEventConnectPipe connect) {
        boolean contains = connectionList.contains(connect.getConnectingPipe().getBehaviour().definition);
        if (blacklist == EnumListStatus.BLACKLIST) {
            if (contains) {
                connect.setAllowed(false);
            }
        } else {// Must be a whitelist
            if (!contains) {
                connect.setAllowed(false);
            }
        }
    }
}
