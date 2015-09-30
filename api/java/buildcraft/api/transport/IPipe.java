/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License. Please check the contents of the license, which
 * should be located as "LICENSE.API" in the BuildCraft source code distribution. */
package buildcraft.api.transport;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import buildcraft.api.gates.IGate;
import buildcraft.api.transport.event.IPipeEvent;

/** SUBCLASSER NOTES: Across ALL buildcraft code {@link #getBehaviour()} and {@link #getTransport()} are always assumed
 * to not be null. You should disallow objects to be made that have a null behaviour or transport object. If you don't
 * implement any behaviour inside of a behaviour object or a transport object, return a generic behaviour object and a
 * generic transport object that does nothing. */
public interface IPipe {
    IPipeTile getTile();

    PipeBehaviour getBehaviour();

    PipeTransport getTransport();

    void postEvent(IPipeEvent event);

    @Deprecated
    IGate getGate(EnumFacing side);

    @Deprecated
    boolean hasGate(EnumFacing side);

    boolean isWired(PipeWire wire);

    boolean isWireActive(PipeWire wire);

    <T> T getProperty(PipeProperty<T> property);

    <T> boolean hasProperty(PipeProperty<T> property);

    void dirtyProperty(PipeProperty<Object> property);

    List<ItemStack> getAllDroppedItems();
}
