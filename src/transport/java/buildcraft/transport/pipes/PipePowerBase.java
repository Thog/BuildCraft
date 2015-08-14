package buildcraft.transport.pipes;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjType;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public abstract class PipePowerBase extends Pipe<PipeTransportPower>implements IMjExternalStorage {
    protected PipePowerBase(Item item) {
        super(new PipeTransportPower(), item);
        transport.initFromPipe(getClass());
    }

    @Override
    public EnumMjType getType() {
        return EnumMjType.TRANSPORT;
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return 0;
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        return 0;
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        return 0;
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {}
}
