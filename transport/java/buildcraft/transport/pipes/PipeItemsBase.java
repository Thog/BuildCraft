package buildcraft.transport.pipes;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

import buildcraft.api.core.IIconProvider;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsBase extends Pipe {
    public final PipeInfo info;

    public PipeItemsBase(Item item, PipeInfo info) {
        super(new PipeTransportItems(), item);
        this.info = info;
    }

    @Override
    public IIconProvider getIconProvider() {
        return BuildCraftTransport.instance.pipeIconProvider;
    }

    @Override
    public int getIconIndex(EnumFacing direction) {
        return PipeIconProvider.getIndex(info);
    }
}
