/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.pipes;

import java.nio.channels.Pipe;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeFactory;
import buildcraft.api.transport.PipeAPI;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.PipeTransportStructure;

public class PipeStructureCobblestone extends Pipe<PipeTransportStructure> {
    private final PipeInfo info;

    public PipeStructureCobblestone(Item item, PipeInfo info) {
        super(new PipeTransportStructure(), item);
        this.info = info;
    }

    public static Item createFactory(final PipeInfo info) {
        final Item item = PipeAPI.REGISTRY.createNewPipeItem();
        // TODO (JDK1.8): Convert this to a lambda
        IPipeFactory factory = new IPipeFactory() {
            @Override
            public IPipe createPipe() {
                return new PipeStructureCobblestone(item, info);
            }
        };
        PipeAPI.REGISTRY.registerFactory(item, factory);
        item.setUnlocalizedName(info.getUnlocalizedName());
        return item;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider() {
        return BuildCraftTransport.instance.pipeIconProvider;
    }

    @Override
    public int getIconIndex(EnumFacing direction) {
        return PipeIconProvider.getIndex(info);
    }
}
