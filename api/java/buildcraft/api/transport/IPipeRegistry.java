package buildcraft.api.transport;

import net.minecraft.item.Item;

import buildcraft.api.ITripleRegistry;

public interface IPipeRegistry extends ITripleRegistry<PipeDefinition> {
    /** Registers a definition, returning the item associated with it. Note that the item has not been registered, so
     * you still need to register it with forge. */
    Item registerDefinition(PipeDefinition definition);
}
