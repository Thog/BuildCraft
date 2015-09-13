/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License. Please check the contents of the license, which
 * should be located as "LICENSE.API" in the BuildCraft source code distribution. */
package buildcraft.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

import buildcraft.api.transport.PipeDefinition;

public class PipePlacedEvent extends Event {
    public EntityPlayer player;
    public PipeDefinition pipeDefinition;
    public BlockPos pos;

    public PipePlacedEvent(EntityPlayer player, PipeDefinition pipeDefinition, BlockPos pos) {
        this.player = player;
        this.pipeDefinition = pipeDefinition;
        this.pos = pos;
    }

}
