package buildcraft.api.transport.pluggable;

import net.minecraft.item.Item;

import buildcraft.api.ITripleRegistry;

public interface IPluggableRegistry extends ITripleRegistry<PluggableDefinition> {
    /** Registers a definition, and the item associated with it. If either is null, a {@link NullPointerException} is
     * thrown */
    void registerDefinition(PluggableDefinition definition, Item item);
}
