package buildcraft.api.transport;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/** An instance is created per pipe block in world, and is registered with the pipe event bus to listen and respond to
 * events. */
public abstract class PipeBehaviour {
    public final PipeDefinition definition;

    /** NEVER SET THIS YOURSELF! This will be null if the pipe has not been properly added to the world yet, however it
     * is GUARRENTEED to not be null for all events fired. (This will always be null for getIconSuffix) */
    public IPipe owner;

    public PipeBehaviour(PipeDefinition definition) {
        this.definition = definition;
    }

    /** Only called instances of IPipe! */
    public void setOwner(IPipe pipe) {
        owner = pipe;
    }

    public abstract NBTTagCompound writeToNBT();

    public abstract void readFromNBT(NBTTagCompound nbt);

    /** @param side The side of which the pipe should be registered
     * @return An integer between 0 (inclusive) and definition.maxSprites (exclusive). */
    public abstract int getIconIndex(EnumFacing side);

    /** Return the index for the icon for items. Override this if getIconIndex(null) does NOT return the item icon */
    public int getIconIndexForItem() {
        return getIconIndex(null);
    }

    /** Get the suffix for the icon of the pipe. This will be appended to the mod-unique tag of the pipe. (So, if this
     * had a mod-unique tag of "cobblestone", and this returned "_north" the resulting location would be
     * "cobblestone_north". The index is guaranteed to be between 0 (inclusive) and your definition's maxSprites number
     * (exclusive). */
    public String getIconSuffix(int index) {
        return "";
    }
}
