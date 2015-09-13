package buildcraft.api.transport.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeBehaviour;

public abstract class PipeEventConnection extends PipeEvent {
    /** Fired whenever a connection could be made. Note that this will never fire if:
     * <ul>
     * <li>The block next to it is not an instance of {@link net.minecraft.block.ITileEntityProvider}</li>
     * <li>The block next to it is an instance of {@link buildcraft.api.transport.IPipeTile} and that pipe tile has a
     * type different to this one.</li>
     * </ul>
     * {@link #allow} will be set to false automatically if {@link #correctType} is false, however if the connecting
     * tile implements IPipeConnection and it returns ConnectionType.CONNECT then it will be set to true. */
    public static abstract class Connect extends PipeEventConnection {
        /** Change this to false if the connection should be refused */
        public boolean allow;
        public final TileEntity with;
        /** Will be true if:
         * <ul>
         * <li>This is an item carrying pipe AND the tile implements IInventory</li>
         * <li>This is a fluid carrying pipe AND the tile implements IFluidHandler</li>
         * <li>This is a power carrying pipe AND the tile implements IMjHandler</li>
         * </ul>
        */
        public final boolean correctType;

        public Connect(EnumFacing side, boolean allow, TileEntity with, boolean correctType) {
            super(side);
            this.allow = allow;
            this.with = with;
            this.correctType = correctType;
        }
    }

    /** Exactly the same as Connect, but fired instead if the tile that could be connected to implements
     * {@link buildcraft.api.transport.IPipeTile} */
    public static class ConnectPipe extends Connect {
        public final PipeBehaviour behaviour;
        public final IPipe pipe;
        public final IPipeTile pipeTile;

        public ConnectPipe(EnumFacing side, boolean allow, TileEntity with, boolean correctType, IPipeTile pipeTile) {
            super(side, allow, with, correctType);
            this.behaviour = pipeTile.getPipe().getBehaviour();
            this.pipe = pipeTile.getPipe();
            this.pipeTile = pipeTile;
        }
    }

    /** Fired specifically whenever the block is NOT a pipe. */
    public static class ConnectBlock extends Connect {
        public ConnectBlock(EnumFacing side, boolean allow, TileEntity with, boolean correctType) {
            super(side, allow, with, correctType);
        }
    }

    /** Fired whenever a pipe has a connection removed. */
    public static class Disconnect extends PipeEventConnection {
        public Disconnect(EnumFacing side) {
            super(side);
        }
    }

    public final EnumFacing side;

    PipeEventConnection(EnumFacing side) {
        this.side = side;
    }
}
