package buildcraft.api.transport.pluggable;

import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;

public interface IPluggableBehaviourFactory {
    PluggableBehaviour createNew(IPipe parent, EnumFacing side);
}
