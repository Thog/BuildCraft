package buildcraft.transport.pipes;

import java.nio.channels.Pipe;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeFactory;
import buildcraft.api.transport.PipeAPI;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.internal.pipes.PipeTransportFluids;

public class PipeFluidsBase extends Pipe<PipeTransportFluids> {
    private final PipeInfo info;

    public PipeFluidsBase(Item item, PipeInfo info) {
        super(new PipeTransportFluids(), item);
        this.info = info;
    }

    public static Item createFactory(final PipeInfo info) {
        final Item item = PipeAPI.registry.createNewPipeItem();
        // TODO (JDK1.8): Convert this to a lambda
        IPipeFactory factory = new IPipeFactory() {
            @Override
            public IPipe createPipe() {
                return new PipeFluidsBase(item, info);
            }
        };
        PipeAPI.registry.registerFactory(item, factory);
        item.setUnlocalizedName(info.getUnlocalizedName());
        return item;
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
