package buildcraft.transport.pipes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;
import buildcraft.api.transport.PipeDefinition;

public class BehaviourStructure extends PipeBehaviour {
    public BehaviourStructure(PipeDefinition definition, IPipeTile pipe) {
        super(definition, pipe);
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
}
