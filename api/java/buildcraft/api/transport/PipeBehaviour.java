package buildcraft.api.transport;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/** An instance is created by instance of IBehaviourFactory per pipe block in world, and is registered with the pipe
 * event bus to listen and respond to events. */
public abstract class PipeBehaviour {
    // Debugging id
    private static volatile int ids = 0;

    private static synchronized int nextId() {
        return ids++;
    }

    public final PipeDefinition definition;
    public final IPipeTile pipe;
    public final int id;

    public PipeBehaviour(PipeDefinition definition, IPipeTile pipe) {
        this.id = nextId();
        if (definition == null) {
            throw new IllegalArgumentException("You cannot pass a null pipe definition!");
        }
        this.definition = definition;
        if (pipe == null) {
            throw new IllegalArgumentException("YOu cannot pass a null pipe tile!");
        }
        this.pipe = pipe;
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
}
