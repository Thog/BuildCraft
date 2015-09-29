package buildcraft.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.event.IPipeEventAttemptConnect;

public abstract class PipeEventAttemptConnect extends PipeEvent implements IPipeEventAttemptConnect {
    private final EnumFacing side;
    private final TileEntity connectingTile;
    private final boolean asked, correctType;
    private boolean allowed;

    PipeEventAttemptConnect(IPipe pipe, EnumFacing side, TileEntity connectingTile, boolean asked, boolean correctType) {
        super(pipe);
        this.side = side;
        this.connectingTile = connectingTile;
        this.asked = asked;
        this.correctType = correctType;
        this.allowed = asked || correctType;
    }

    /** NOTE: If both {@link #asked} and {@link #correctType} are false, this event will always disallow the connection
     * (As there is intentionally no way of allowing the connection, only disallowing it) */
    public static PipeEventAttemptConnect createEvent(IPipe thisPipe, EnumFacing side, TileEntity connectingTile, boolean asked,
            boolean correctType) {
        if (connectingTile instanceof IPipeTile) {
            IPipeTile otherTile = (IPipeTile) connectingTile;
            IPipe pipe = otherTile.getPipe();
            if (pipe != null) {
                return new PipeEventAttemptConnectPipe(thisPipe, side, connectingTile, correctType, correctType, pipe);
            }
        }
        return new PipeEventAttemptConnectBlock(thisPipe, side, connectingTile, correctType, correctType);
    }

    @Override
    public EnumFacing getConnectingSide() {
        return side;
    }

    @Override
    public TileEntity getConnectingTile() {
        return connectingTile;
    }

    @Override
    public boolean hasAskedForConnection() {
        return asked;
    }

    @Override
    public boolean isCorrectType() {
        return correctType;
    }

    @Override
    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public void disallow() {
        allowed = false;
    }
}
