/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.factory.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.blueprints.BuilderAPI;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.api.tiles.IControllable;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.core.BuildCraftCore;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.utils.BlockMiner;
import buildcraft.core.lib.utils.BlockUtils;
import buildcraft.factory.BuildCraftFactory;

import io.netty.buffer.ByteBuf;

public class TileMiningWell extends TileBuildCraft implements IHasWork, IPipeConnection, IControllable, IMjHandler {

    private static final double MAX_POWER = 2 * 64 * BuilderAPI.BREAK_ENERGY * BuildCraftCore.miningMultiplier;
    private static final double MAX_TRANSFERED = 4 * BuilderAPI.BREAK_ENERGY * BuildCraftCore.miningMultiplier;
    private static final double POWER_ACTIVATION = 4 * BuilderAPI.BREAK_ENERGY;
    private static final long LOSS_DELAY = 400;
    private static final double LOSS_RATE = BuilderAPI.BREAK_ENERGY * BuildCraftCore.miningMultiplier / 4;

    private boolean isDigging = true;
    private BlockMiner miner;
    private int ledState;
    private int ticksSinceAction = 9001;

    private SafeTimeTracker updateTracker = new SafeTimeTracker(BuildCraftCore.updateFactor);

    private final DefaultMjExternalStorage externalStorage;
    private final DefaultMjInternalStorage internalStorage;

    public TileMiningWell() {
        super();
        externalStorage = new DefaultMjExternalStorage(EnumMjDevice.MACHINE, EnumMjPower.NORMAL, MAX_TRANSFERED);
        internalStorage = new DefaultMjInternalStorage(MAX_POWER, POWER_ACTIVATION, LOSS_DELAY, LOSS_RATE);
        externalStorage.setInternalStorage(internalStorage);
    }

    /** Dig the next available piece of land if not done. As soon as it reaches bedrock, lava or goes below 0, it's
     * considered done. */
    @Override
    public void update() {
        super.update();

        if (worldObj.isRemote) {
            return;
        }

        internalStorage.tick(getWorld());

        if (updateTracker.markTimeIfDelay(worldObj)) {
            sendNetworkUpdate();
        }

        ticksSinceAction++;

        if (mode == Mode.Off) {
            if (miner != null) {
                miner.invalidate();
                miner = null;
            }
            isDigging = false;
            return;
        }

        if (!internalStorage.hasActivated()) {
            return;
        }

        if (miner == null) {
            World world = worldObj;

            BlockPos search = pos.down();

            while (world.getBlockState(search).getBlock() == BuildCraftFactory.plainPipeBlock) {
                search = search.down();
            }

            if (search.getY() < 1 || search.getY() < pos.getY() - BuildCraftFactory.miningDepth || !BlockUtils.canChangeBlock(world, search)) {
                isDigging = false;
                internalStorage.stopOperating();
                return;
            }

            if (world.isAirBlock(search) || world.getBlockState(search).getBlock().isReplaceable(world, search)) {
                ticksSinceAction = 0;
                world.setBlockState(search, BuildCraftFactory.plainPipeBlock.getDefaultState());
            } else {
                miner = new BlockMiner(world, this, search);
            }
        }

        if (miner != null) {
            isDigging = true;
            ticksSinceAction = 0;

            double usedEnergy = miner.acceptPower(internalStorage.currentPower());
            internalStorage.extractPower(getWorld(), usedEnergy, usedEnergy, false);

            if (miner.hasMined()) {
                if (miner.hasFailed()) {
                    isDigging = false;
                }
                miner = null;
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (miner != null) {
            miner.invalidate();
        }
        if (worldObj != null && pos.getY() > 2) {
            BuildCraftFactory.miningWellBlock.removePipes(worldObj, pos);
        }
    }

    @Override
    public void writeData(ByteBuf stream) {
        super.writeData(stream);

        ledState = (ticksSinceAction < 2 ? 16 : 0) | (int) (internalStorage.currentPower() * 15 / internalStorage.maxPower());
        stream.writeByte(ledState);
    }

    @Override
    public void readData(ByteBuf stream) {
        super.readData(stream);

        int newLedState = stream.readUnsignedByte();
        if (newLedState != ledState) {
            ledState = newLedState;
            worldObj.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
    }

    public int getIconGlowLevel(int renderPass) {
        if (renderPass == 2) { // Red LED
            return ledState & 15;
        } else if (renderPass == 3) { // Green LED
            return (ledState >> 4) > 0 ? 15 : 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean hasWork() {
        return isDigging;
    }

    @Override
    public ConnectOverride overridePipeConnection(EnumPipeType type, EnumFacing with) {
        if (BuildCraftProperties.BLOCK_FACING.getValue(worldObj.getBlockState(pos)) == with) {
            return ConnectOverride.DISCONNECT;
        }
        return type == EnumPipeType.ITEM ? ConnectOverride.CONNECT : ConnectOverride.DEFAULT;
    }

    @Override
    public boolean acceptsControlMode(Mode mode) {
        return mode == Mode.Off || mode == Mode.On;
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }
}
