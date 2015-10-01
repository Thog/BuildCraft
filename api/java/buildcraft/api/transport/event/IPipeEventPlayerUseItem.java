package buildcraft.api.transport.event;

import net.minecraft.item.ItemStack;

import buildcraft.api.tools.IToolWrench;

/** Fired whenever a player right clicks the pipe with an item that is not an {@link IToolWrench}. */
public interface IPipeEventPlayerUseItem extends IPipeEventPlayerInteract {
    ItemStack getItemStack();
}
