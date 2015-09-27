package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventConnect;

public class PipeEventConnect extends PipeEventConnection implements IPipeEventConnect {
    private final boolean askedForConnection;
    private final boolean isCorrectType;
    boolean allowed;

    public PipeEventConnect(IPipe pipe, EnumFacing side, TileEntity tile, boolean askedForConnection, boolean isCorrectType) {
        super(pipe, side, tile);
        this.askedForConnection = askedForConnection;
        this.isCorrectType = isCorrectType;
        allowed = askedForConnection || isCorrectType;
    }

    @Override
    public boolean hasAskedForConnection() {
        return askedForConnection;
    }

    @Override
    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public void setAllowed(boolean allow) {
        allowed = allow;
    }

    @Override
    public boolean isCorrectType() {
        return isCorrectType;
    }
}
