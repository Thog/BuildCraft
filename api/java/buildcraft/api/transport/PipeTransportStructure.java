/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.api.transport;

import java.util.Collections;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class PipeTransportStructure extends PipeTransport {
    public PipeTransportStructure(IPipeTile tile) {
        super(tile);
    }

    @Override
    public EnumPipeType getPipeType() {
        return EnumPipeType.STRUCTURE;
    }

    @Override
    public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
        if (tile instanceof IPipeTile) {
            IPipe pipe2 = ((IPipeTile) tile).getPipe();

            return pipe2.getTransport() instanceof PipeTransportStructure;
        }

        return false;
    }

    @Override
    public List<PipeProperty<?>> getAllProperties() {
        return Collections.emptyList();
    }

    @Override
    public void renderTransport(float partialTicks) {

    }
}
