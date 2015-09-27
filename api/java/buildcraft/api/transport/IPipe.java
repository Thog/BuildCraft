/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License. Please check the contents of the license, which
 * should be located as "LICENSE.API" in the BuildCraft source code distribution. */
package buildcraft.api.transport;

import net.minecraft.util.EnumFacing;

import buildcraft.api.gates.IGate;
import buildcraft.api.transport.event.IPipeEvent;
import buildcraft.transport.PipeTransport;

public interface IPipe {
    IPipeTile getTile();

    PipeBehaviour getBehaviour();

    PipeTransport getTransport();

    void postEvent(IPipeEvent event);

    IGate getGate(EnumFacing side);

    boolean hasGate(EnumFacing side);

    boolean isWired(PipeWire wire);

    boolean isWireActive(PipeWire wire);

    <T> T getProperty(PipeProperty<T> property);

    <T> boolean hasProperty(PipeProperty<T> property);

    void dirtyProperty(PipeProperty<Object> property);
}
