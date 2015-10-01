/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.internal.pipes;

import java.util.EnumSet;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.BCLog;
import buildcraft.api.core.ISerializable;
import buildcraft.api.gates.IGateExpansion;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.transport.*;
import buildcraft.api.transport.IPipeConnection.ConnectOverride;
import buildcraft.api.transport.event.IPipeEventAttemptConnectPipe;
import buildcraft.api.transport.pluggable.IFacadePluggable;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.BuildCraftCore;
import buildcraft.core.DefaultProps;
import buildcraft.core.internal.IDropControlInventory;
import buildcraft.core.lib.ITileBufferHolder;
import buildcraft.core.lib.TileBuffer;
import buildcraft.core.lib.block.IAdditionalDataTile;
import buildcraft.core.lib.network.IGuiReturnHandler;
import buildcraft.core.lib.network.ISyncedTile;
import buildcraft.core.lib.network.Packet;
import buildcraft.core.lib.network.PacketTileState;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.FacadePluggable;
import buildcraft.transport.Gate;
import buildcraft.transport.ISolidSideTile;
import buildcraft.transport.PipePluggableState;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.event.PipeEventAttemptConnect;
import buildcraft.transport.event.PipeEventConnect;
import buildcraft.transport.event.PipeEventDisconnect;
import buildcraft.transport.gates.GateFactory;
import buildcraft.transport.gates.GatePluggable;
import buildcraft.transport.item.ItemFacade.FacadeState;

import io.netty.buffer.ByteBuf;

public class TileGenericPipe extends TileEntity implements IUpdatePlayerListBox, IFluidHandler, IPipeTile, ITileBufferHolder, IDropControlInventory,
        ISyncedTile, ISolidSideTile, IGuiReturnHandler, IDebuggable, IAdditionalDataTile, IMjHandler {

    public boolean initialized = false;
    public final PipeRenderState renderState = new PipeRenderState();
    public final PipePluggableState pluggableState = new PipePluggableState();
    public final CoreState coreState = new CoreState();

    private final EnumSet<EnumFacing> pipeConnections = EnumSet.noneOf(EnumFacing.class);

    Pipe pipe;
    public int redstoneInput;
    public int[] redstoneInputSide = new int[EnumFacing.VALUES.length];

    protected boolean deletePipe = false;
    protected boolean sendClientUpdate = false;
    protected boolean blockNeighborChange = false;
    protected boolean refreshRenderState = false;
    protected boolean pipeBound = false;
    protected boolean resyncGateExpansions = false;
    protected boolean attachPluggables = false;
    protected SideProperties sideProperties = new SideProperties();

    private final SidedExternalStorage storage = new SidedExternalStorage(this);

    private TileBuffer[] tileBuffer;
    private int glassColor = -1;

    public TileGenericPipe() {}

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (glassColor >= 0) {
            nbt.setByte("stainedColor", (byte) glassColor);
        }
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            final String key = "redstoneInputSide[" + i + "]";
            nbt.setByte(key, (byte) redstoneInputSide[i]);
        }

        NBTTagCompound connections = new NBTTagCompound();
        for (EnumFacing face : EnumFacing.values()) {
            connections.setBoolean(face.name(), pipeConnections.contains(face));
        }
        nbt.setTag("connections", connections);

        nbt.setInteger("pipeId", Item.getIdFromItem(PipeAPI.REGISTRY.getItem(pipe.getBehaviour().definition)));

        sideProperties.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        glassColor = nbt.hasKey("stainedColor") ? nbt.getByte("stainedColor") : -1;

        redstoneInput = 0;

        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            final String key = "redstoneInputSide[" + i + "]";
            if (nbt.hasKey(key)) {
                redstoneInputSide[i] = nbt.getByte(key);

                if (redstoneInputSide[i] > redstoneInput) {
                    redstoneInput = redstoneInputSide[i];
                }
            } else {
                redstoneInputSide[i] = 0;
            }
        }

        NBTTagCompound connections = nbt.getCompoundTag("connections");
        pipeConnections.clear();
        for (EnumFacing face : EnumFacing.values()) {
            if (connections.getBoolean(face.name())) {
                pipeConnections.add(face);
            }
        }

        int id = nbt.getInteger("pipeId");
        PipeDefinition definition = PipeAPI.REGISTRY.getDefinition(Item.getItemById(id));

        if (definition == null) {
            this.deletePipe = true;
            new RuntimeException("Definition was null! (id = " + id + ", item = " + Item.getItemById(id) + ")").printStackTrace();;
        } else {
            coreState.pipeId = Item.getIdFromItem(PipeAPI.REGISTRY.getItem(definition));

            pipe = new Pipe(definition, this);
            bindPipe();

            if (pipe != null) {
                pipe.readFromNBT(nbt);
            } else {
                BCLog.logger.log(Level.WARN, "Pipe failed to load from NBT at " + getPos() + ", deleting");
                deletePipe = true;
            }

            sideProperties.readFromNBT(nbt);
            attachPluggables = true;
        }
    }

    @Override
    public void invalidate() {
        initialized = false;
        tileBuffer = null;

        if (pipe != null) {
            pipe.invalidate();
        }

        sideProperties.invalidate();

        super.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
        initialized = false;
        tileBuffer = null;
        bindPipe();

        if (pipe != null) {
            pipe.validate();
        }

        sideProperties.validate(this);
    }

    protected void notifyBlockChanged() {
        worldObj.notifyBlockOfStateChange(getPos(), getBlock());
        scheduleRenderUpdate();
        sendNetworkUpdate();
        if (pipe != null) {
            BlockGenericPipe.updateNeighbourSignalState(pipe);
        }
    }

    @Override
    public void update() {
        try {// Defensive code against errors in implementors
            if (!worldObj.isRemote) {
                if (deletePipe) {
                    worldObj.setBlockToAir(getPos());
                    return;
                }

                if (pipe == null) {
                    return;
                }

                if (!initialized) {
                    initialize(pipe.getBehaviour().definition);
                }
            }

            if (attachPluggables) {
                attachPluggables = false;
                // Attach callback
                for (int i = 0; i < EnumFacing.VALUES.length; i++) {
                    if (sideProperties.pluggables[i] != null) {
                        pipe.eventBus.register(sideProperties.pluggables[i]);
                        sideProperties.pluggables[i].onAttachedPipe(this, EnumFacing.getFront(i));
                    }
                }
                notifyBlockChanged();
            }

            if (!BlockGenericPipe.isValid(pipe)) {
                return;
            }

            pipe.update();

            for (EnumFacing direction : EnumFacing.VALUES) {
                PipePluggable p = getPipePluggable(direction);
                if (p != null) {
                    p.update(this, direction);
                }
            }

            if (worldObj.isRemote) {
                if (resyncGateExpansions) {
                    syncGateExpansions();
                }

                return;
            }

            if (blockNeighborChange) {
                if (computeConnections()) {
                    for (EnumFacing face : EnumFacing.values()) {
                        TileEntity tile = worldObj.getTileEntity(getPos().offset(face));
                        if (tile instanceof TileGenericPipe) {
                            ((TileGenericPipe) tile).scheduleNeighborChange();
                        }
                    }
                }
                pipe.onNeighborBlockChange(0);
                blockNeighborChange = false;
                refreshRenderState = true;
            }

            if (refreshRenderState) {
                if (refreshRenderState()) {
                    for (EnumFacing face : EnumFacing.values()) {
                        TileEntity tile = worldObj.getTileEntity(getPos().offset(face));
                        if (tile instanceof TileGenericPipe) {
                            ((TileGenericPipe) tile).scheduleRenderUpdate();
                        }
                    }
                }
                refreshRenderState = false;
            }

            if (sendClientUpdate) {
                sendClientUpdate = false;
                Packet packet = getBCDescriptionPacket();
                BuildCraftCore.instance.sendToPlayersNear(packet, this);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    public void initializeFromItemMetadata(int i) {
        if (i >= 1 && i <= 16) {
            setPipeColor((i - 1) & 15);
        } else {
            setPipeColor(-1);
        }
    }

    public int getItemMetadata() {
        return getPipeColor() >= 0 ? (1 + getPipeColor()) : 0;
    }

    @Override
    public int getPipeColor() {
        return worldObj.isRemote ? renderState.getGlassColor() : this.glassColor;
    }

    public boolean setPipeColor(int color) {
        if (!worldObj.isRemote && color >= -1 && color < 16 && glassColor != color) {
            renderState.glassColorDirty = true;
            glassColor = color;
            notifyBlockChanged();
            worldObj.notifyBlockOfStateChange(pos, blockType);
            return true;
        }
        return false;
    }

    /** PRECONDITION: worldObj must not be null
     * 
     * @return <code>True</code> if any part of the render state changed */
    protected boolean refreshRenderState() {
        renderState.setGlassColor((byte) glassColor);

        // Pipe connections;
        for (EnumFacing o : EnumFacing.VALUES) {
            renderState.pipeConnectionMatrix.setConnected(o, pipeConnections.contains(o));
            if (pipeConnections.contains(o)) {
                BlockPos connected = getPos().offset(o);
                IBlockState state = worldObj.getBlockState(connected);
                Block block = state.getBlock();
                ICustomPipeConnection connection = PipeConnectionAPI.getCustomConnection(block);
                renderState.setExtension(o, connection.getExtension(worldObj, connected, o, state));
            }
        }

        // Pipe Textures
        for (int i = 0; i < 7; i++) {
            EnumFacing o = EnumFacing.getFront(i);
            renderState.textureMatrix.setIconIndex(o, pipe.getBehaviour().getIconIndex(o));
        }

        // WireState
        for (PipeWire color : PipeWire.values()) {
            renderState.wireMatrix.setWire(color, pipe.wireSet[color.ordinal()]);

            for (EnumFacing direction : EnumFacing.VALUES) {
                renderState.wireMatrix.setWireConnected(color, direction, pipe.isWireConnectedTo(this.getTile(direction), color, direction));
            }

            boolean lit = pipe.signalStrength[color.ordinal()] > 0;
            renderState.wireMatrix.setWireLit(color, lit);
        }

        // Facades
        for (EnumFacing direction : EnumFacing.VALUES) {
            PipePluggable pluggable = sideProperties.pluggables[direction.ordinal()];
            if (!(pluggable instanceof FacadePluggable)) {
                continue;
            }

            FacadeState[] states = ((FacadePluggable) pluggable).states;
            // Iterate over all states and activate first proper
            int defaultState = -1;
            int activeState = -1;
            for (int i = 0; i < states.length; i++) {
                FacadeState state = states[i];
                if (state.wire == null) {
                    defaultState = i;
                    continue;
                }
                if (pipe != null && pipe.isWireActive(state.wire)) {
                    activeState = i;
                    break;
                }
            }
            if (activeState < 0) {
                activeState = defaultState;
            }
            ((FacadePluggable) pluggable).setActiveState(activeState);
        }

        /* TODO: Rewrite the requiresRenderUpdate API to run on the server side instead of the client side to save
         * network bandwidth */
        pluggableState.setPluggables(sideProperties.pluggables);

        boolean changed = renderState.isDirty();
        // TODO (Pass 1): If the pluggable state has changed, also update it!

        if (renderState.isDirty()) {
            renderState.clean();
        }
        sendNetworkUpdate();
        return changed;
    }

    public void initialize(PipeDefinition definition) {
        this.blockType = getBlockType();

        pipe = new Pipe(definition, this);

        for (EnumFacing o : EnumFacing.VALUES) {
            TileEntity tile = getTile(o);

            if (tile instanceof ITileBufferHolder) {
                ((ITileBufferHolder) tile).blockCreated(o, BuildCraftTransport.genericPipeBlock, this);
            }
            if (tile instanceof IPipeTile) {
                ((IPipeTile) tile).scheduleNeighborChange();
            }
        }

        bindPipe();

        scheduleNeighborChange();
        scheduleRenderUpdate();

        if (!pipe.isInitialized()) {
            pipe.initialize();
        }

        initialized = true;
    }

    private void bindPipe() {
        if (!pipeBound && pipe != null) {
            coreState.setDefinition(pipe.getBehaviour().definition);
            pipeBound = true;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void scheduleNeighborChange() {
        blockNeighborChange = true;
    }

    @Override
    public boolean canInjectItems(EnumFacing from) {
        if (getPipeType() != EnumPipeType.ITEM) {
            return false;
        }
        return isPipeConnected(from);
    }

    @Override
    public int injectItem(ItemStack payload, boolean doAdd, EnumFacing from, EnumDyeColor color) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof PipeTransportItems && isPipeConnected(from) && pipe.getTransport()
                .inputOpen(from)) {

            if (doAdd) {
                Vec3 itemPos = Utils.convertMiddle(getPos()).add(Utils.convert(from, 0.4));

                TravelingItem pipedItem = TravelingItem.make(itemPos, payload);
                pipedItem.color = color;
                ((PipeTransportItems) pipe.getTransport()).injectItem(pipedItem, from.getOpposite());
            }
            return payload.stackSize;
        }

        return 0;
    }

    @Override
    public IPipeType getPipeType() {
        if (BlockGenericPipe.isValid(pipe)) {
            return pipe.getTransport().getPipeType();
        }
        return null;
    }

    /* SMP */

    public Packet getBCDescriptionPacket() {
        bindPipe();
        updateCoreState();

        if (pipe == null) {
            // If the pipe is null it will cause major problems for the client, so lets just not send it
            return null;
        }
        PacketTileState packet = new PacketTileState(this);
        packet.tempWorld = worldObj;

        if (pipe != null && pipe.getTransport() != null) {
            pipe.getTransport().sendDescriptionPacket();
        }

        packet.addStateForSerialization((byte) 0, coreState);
        packet.addStateForSerialization((byte) 1, renderState);
        packet.addStateForSerialization((byte) 2, pluggableState);

        if (pipe != null && pipe.getBehaviour() instanceof ISerializable) {
            packet.addStateForSerialization((byte) 3, (ISerializable) pipe.getBehaviour());
        }

        return packet;
    }

    @Override
    public net.minecraft.network.Packet getDescriptionPacket() {
        // Bit nasty, but this ensures that we only have one way of writing packets
        sendNetworkUpdate();
        return null;
    }

    @Override
    public void sendNetworkUpdate() {
        sendClientUpdate = true;
    }

    @Override
    public void blockRemoved(EnumFacing from) {

    }

    public TileBuffer[] getTileCache() {
        if (tileBuffer == null && pipe != null) {
            tileBuffer = TileBuffer.makeBuffer(worldObj, getPos(), pipe.getTransport().delveIntoUnloadedChunks());
        }
        return tileBuffer;
    }

    @Override
    public void blockCreated(EnumFacing from, Block block, TileEntity tile) {
        TileBuffer[] cache = getTileCache();
        if (cache != null) {
            cache[from.getOpposite().ordinal()].set(block.getDefaultState(), tile);
        }
    }

    @Override
    public Block getBlock(EnumFacing to) {
        TileBuffer[] cache = getTileCache();
        if (cache != null) {
            return cache[to.ordinal()].getBlockState().getBlock();
        } else {
            return null;
        }
    }

    @Override
    public TileEntity getTile(EnumFacing to) {
        return getTile(to, false);
    }

    public TileEntity getTile(EnumFacing to, boolean forceUpdate) {
        TileBuffer[] cache = getTileCache();
        if (cache != null) {
            return cache[to.ordinal()].getTile(forceUpdate);
        } else {
            return null;
        }
    }

    protected boolean canPipeConnect_internal(TileEntity with, EnumFacing side) {
        boolean askedForConnection = false;

        if (with instanceof IPipeConnection) {
            IPipeConnection.ConnectOverride override = ((IPipeConnection) with).overridePipeConnection(pipe.getTransport().getPipeType(), side
                    .getOpposite());
            if (override == ConnectOverride.DISCONNECT) {
                return false;
            } else if (override == ConnectOverride.CONNECT) {
                askedForConnection = true;
            }
        }

        boolean isRightType = pipe.getTransport().canPipeConnect(with, side);
        PipeEventAttemptConnect event = PipeEventAttemptConnect.createEvent(pipe, side, with, askedForConnection, isRightType);

        if (event instanceof IPipeEventAttemptConnectPipe) {
            // Now ask the other pipe

            // This pipe will never ask to override the connection
            askedForConnection = false;

            IPipe otherPipe = ((IPipeTile) with).getPipe();
            isRightType = otherPipe.getTransport().canPipeConnect(this, side.getOpposite());

            PipeEventAttemptConnect otherEvent = PipeEventAttemptConnect.createEvent(otherPipe, side.getOpposite(), this, askedForConnection,
                    isRightType);
            otherPipe.postEvent(otherEvent);
            if (!otherEvent.isAllowed()) {
                return false;
            }
        }

        pipe.postEvent(event);

        return event.isAllowed();
    }

    /** Checks if this tile can connect to another tile
     *
     * @param with - The other Tile
     * @param side - The orientation to get to the other tile ('with')
     * @return true if pipes are considered connected */
    protected boolean canPipeConnect(TileEntity with, EnumFacing side) {
        if (with == null) {
            return false;
        }

        if (hasBlockingPluggable(side)) {
            return false;
        }

        if (!BlockGenericPipe.isValid(pipe)) {
            return false;
        }

        // This is called when the other pipe may not have a world yet, and so it crashes
        if (!with.hasWorldObj() && hasWorldObj()) {
            with.setWorldObj(worldObj);
        }

        return canPipeConnect_internal(with, side);
    }

    @Deprecated
    public boolean hasBlockingPluggable(EnumFacing side) {
        PipePluggable pluggable = getPipePluggable(side);
        if (pluggable == null) {
            return false;
        }

        if (pluggable instanceof IPipeConnection) {
            IPipe neighborPipe = getNeighborPipe(side);
            if (neighborPipe != null) {
                IPipeConnection.ConnectOverride override = ((IPipeConnection) pluggable).overridePipeConnection(neighborPipe.getTile().getPipeType(),
                        side);
                if (override == IPipeConnection.ConnectOverride.CONNECT) {
                    return true;
                } else if (override == IPipeConnection.ConnectOverride.DISCONNECT) {
                    return false;
                }
            }
        }
        return pluggable.isBlocking(this, side);
    }

    protected boolean computeConnections() {
        if (worldObj.isRemote) {
            return false;
        }
        TileBuffer[] cache = getTileCache();
        if (cache == null) {
            return false;
        }

        boolean changed = false;
        for (EnumFacing side : EnumFacing.VALUES) {
            // TileBuffer t = cache[side.ordinal()];
            // For blocks which are not loaded, keep the old connection value.
            // if (t.exists() || !initialized) {
            // t.refresh();
            TileEntity with = worldObj.getTileEntity(pos.offset(side));

            boolean before = pipeConnections.contains(side);
            boolean now = canPipeConnect(with, side);
            if (before != now) {
                if (now) {
                    pipe.postEvent(PipeEventConnect.create(pipe, side, with));
                    pipeConnections.add(side);
                } else {
                    pipe.postEvent(new PipeEventDisconnect(pipe, side, with));
                    pipeConnections.remove(side);
                }
                changed = true;
            }
            // }
        }
        return changed;
    }

    @Override
    public boolean isPipeConnected(EnumFacing with) {
        if (worldObj.isRemote) {
            return renderState.pipeConnectionMatrix.isConnected(with);
        }
        return pipeConnections.contains(with);
    }

    @Override
    public boolean doDrop() {
        if (BlockGenericPipe.isValid(pipe)) {
            return pipe.doDrop();
        } else {
            return false;
        }
    }

    @Override
    public void onChunkUnload() {
        if (pipe != null) {
            pipe.onChunkUnload();
        }
    }

    /** ITankContainer implementation * */
    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof IFluidHandler && !hasBlockingPluggable(from)) {
            return ((IFluidHandler) pipe.getTransport()).fill(from, resource, doFill);
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof IFluidHandler && !hasBlockingPluggable(from)) {
            return ((IFluidHandler) pipe.getTransport()).drain(from, maxDrain, doDrain);
        } else {
            return null;
        }
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof IFluidHandler && !hasBlockingPluggable(from)) {
            return ((IFluidHandler) pipe.getTransport()).drain(from, resource, doDrain);
        } else {
            return null;
        }
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof IFluidHandler && !hasBlockingPluggable(from)) {
            return ((IFluidHandler) pipe.getTransport()).canFill(from, fluid);
        } else {
            return false;
        }
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getTransport() instanceof IFluidHandler && !hasBlockingPluggable(from)) {
            return ((IFluidHandler) pipe.getTransport()).canDrain(from, fluid);
        } else {
            return false;
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return null;
    }

    public void scheduleRenderUpdate() {
        refreshRenderState = true;
    }

    public boolean hasFacade(EnumFacing direction) {
        if (direction == null || direction == null) {
            return false;
        } else {
            return sideProperties.pluggables[direction.ordinal()] instanceof IFacadePluggable;
        }
    }

    public boolean hasGate(EnumFacing direction) {
        if (direction == null || direction == null) {
            return false;
        } else {
            return sideProperties.pluggables[direction.ordinal()] instanceof GatePluggable;
        }
    }

    public boolean setPluggable(EnumFacing direction, PipePluggable pluggable) {
        return setPluggable(direction, pluggable, null);
    }

    public boolean setPluggable(EnumFacing direction, PipePluggable pluggable, EntityPlayer player) {
        if (worldObj != null && worldObj.isRemote) {
            return false;
        }

        if (direction == null || direction == null) {
            return false;
        }

        // Remove old pluggable
        if (sideProperties.pluggables[direction.ordinal()] != null) {
            sideProperties.dropItem(this, direction, player);
            pipe.eventBus.unregister(sideProperties.pluggables[direction.ordinal()]);
        }

        sideProperties.pluggables[direction.ordinal()] = pluggable;
        if (pluggable != null) {
            pipe.eventBus.register(pluggable);
            pluggable.onAttachedPipe(this, direction);
        }
        notifyBlockChanged();
        return true;
    }

    protected void updateCoreState() {}

    public boolean hasEnabledFacade(EnumFacing direction) {
        return hasFacade(direction) && !((FacadePluggable) getPipePluggable(direction)).isTransparent();
    }

    // Legacy
    public void setGate(Gate gate, int direction) {
        if (sideProperties.pluggables[direction] == null) {
            gate.setDirection(EnumFacing.getFront(direction));
            pipe.gates[direction] = gate;
            sideProperties.pluggables[direction] = new GatePluggable(gate);
        }
    }

    @Override
    public ISerializable getStateInstance(byte stateId) {
        switch (stateId) {
            case 0:
                return coreState;
            case 1:
                return renderState;
            case 2:
                return pluggableState;
            case 3:
                return (ISerializable) pipe.getBehaviour();
        }
        throw new RuntimeException("Unknown state requested: " + stateId + " this is a bug!");
    }

    @Override
    public void afterStateUpdated(byte stateId) {
        if (!worldObj.isRemote) {
            return;
        }
        switch (stateId) {
            case 0:
                if (pipe != null) {
                    break;
                }

                if (pipe == null && coreState.pipeId != 0) {
                    initialize(coreState.getDefinition());
                }

                if (pipe == null) {
                    break;
                }

                worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
                break;

            case 1: {
                if (renderState.needsRenderUpdate()) {
                    renderState.clean();
                    worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
                }
                break;
            }
            case 2: {
                PipePluggable[] newPluggables = pluggableState.getPluggables();

                // mark for render update if necessary
                for (int i = 0; i < EnumFacing.VALUES.length; i++) {
                    PipePluggable old = sideProperties.pluggables[i];
                    PipePluggable newer = newPluggables[i];
                    if (old == null && newer == null) {
                        continue;
                    } else if (old != null && newer != null && old.getClass() == newer.getClass()) {
                        if (newer.requiresRenderUpdate(old)) {
                            worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
                            break;
                        }
                    } else {
                        // one of them is null but not the other, so update
                        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
                        break;
                    }
                }
                sideProperties.pluggables = newPluggables.clone();

                for (int i = 0; i < EnumFacing.VALUES.length; i++) {
                    final PipePluggable pluggable = getPipePluggable(EnumFacing.getFront(i));
                    if (pluggable != null && pluggable instanceof GatePluggable) {
                        final GatePluggable gatePluggable = (GatePluggable) pluggable;
                        Gate gate = pipe.gates[i];
                        if (gate == null || gate.logic != gatePluggable.getLogic() || gate.material != gatePluggable.getMaterial()) {
                            pipe.gates[i] = GateFactory.makeGate(pipe, gatePluggable.getMaterial(), gatePluggable.getLogic(), EnumFacing.getFront(i));
                        }
                    } else {
                        pipe.gates[i] = null;
                    }
                }

                syncGateExpansions();
                break;
            }
        }
    }

    private void syncGateExpansions() {
        resyncGateExpansions = false;
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            Gate gate = pipe.gates[i];
            if (gate == null) {
                continue;
            }
            GatePluggable gatePluggable = (GatePluggable) sideProperties.pluggables[i];
            if (gatePluggable.getExpansions().length > 0) {
                for (IGateExpansion expansion : gatePluggable.getExpansions()) {
                    if (expansion != null) {
                        if (!gate.expansions.containsKey(expansion)) {
                            gate.addGateExpansion(expansion);
                        }
                    } else {
                        resyncGateExpansions = true;
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return DefaultProps.PIPE_CONTENTS_RENDER_DIST * DefaultProps.PIPE_CONTENTS_RENDER_DIST;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean isSolidOnSide(EnumFacing side) {
        if (hasPipePluggable(side) && getPipePluggable(side).isSolidOnSide(this, side)) {
            return true;
        }

        if (BlockGenericPipe.isValid(pipe) && pipe.getBehaviour() instanceof ISolidSideTile) {
            if (((ISolidSideTile) pipe.getBehaviour()).isSolidOnSide(side)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PipePluggable getPipePluggable(EnumFacing side) {
        if (side == null) {
            return null;
        }

        return sideProperties.pluggables[side.ordinal()];
    }

    @Override
    public boolean hasPipePluggable(EnumFacing side) {
        if (side == null) {
            return false;
        }

        return sideProperties.pluggables[side.ordinal()] != null;
    }

    public Block getBlock() {
        return getBlockType();
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(getPos()) == this;
    }

    @Override
    public void writeGuiData(ByteBuf data) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getBehaviour() instanceof IGuiReturnHandler) {
            ((IGuiReturnHandler) pipe.getBehaviour()).writeGuiData(data);
        }
    }

    @Override
    public void readGuiData(ByteBuf data, EntityPlayer sender) {
        if (BlockGenericPipe.isValid(pipe) && pipe.getBehaviour() instanceof IGuiReturnHandler) {
            ((IGuiReturnHandler) pipe.getBehaviour()).readGuiData(data, sender);
        }
    }

    @Override
    public Block getNeighborBlock(EnumFacing dir) {
        return getBlock(dir);
    }

    @Override
    public TileEntity getNeighborTile(EnumFacing dir) {
        return getTile(dir);
    }

    @Override
    public IPipe getNeighborPipe(EnumFacing dir) {
        TileEntity neighborTile = getTile(dir);
        if (neighborTile instanceof IPipeTile) {
            return ((IPipeTile) neighborTile).getPipe();
        } else {
            return null;
        }
    }

    @Override
    public IPipe getPipe() {
        return pipe;
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        if (pipe == null) {
            return;
        }

        left.add("");
        for (EnumFacing face : EnumFacing.values()) {
            left.add(face.getName() + " = " + isPipeConnected(face));
        }

        if (pipe.getBehaviour() instanceof IDebuggable) {
            ((IDebuggable) pipe.getBehaviour()).getDebugInfo(left, right, side);
        }
        if (pipe.getTransport() instanceof IDebuggable) {
            ((IDebuggable) pipe.getTransport()).getDebugInfo(left, right, side);
        }
        if (getPipePluggable(side) != null && getPipePluggable(side) instanceof IDebuggable) {
            ((IDebuggable) getPipePluggable(side)).getDebugInfo(left, right, side);
        }
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return storage;
    }
}
