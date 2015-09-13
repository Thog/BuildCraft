/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import buildcraft.api.enums.EnumEnergyStage;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjConnection;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.transport.EnumPipeType;
import buildcraft.core.lib.engines.TileEngineBase;

public class TileEngineWood extends TileEngineBase {
    protected class SidedMjExternalStorage extends DefaultMjExternalStorage implements IMjConnection {
        public SidedMjExternalStorage(double maxPowerTransfered) {
            super(EnumMjDevice.ENGINE, EnumMjPower.REDSTONE, maxPowerTransfered);
            addLimiter(new IConnectionLimiter() {
                @Override
                public boolean allowConnection(World world, EnumFacing flowDirection, IMjExternalStorage from, IMjExternalStorage to,
                        boolean flowingIn) {
                    return orientation == flowDirection;
                }
            });
        }

        @Override
        public boolean canConnectPower(EnumFacing face, IMjExternalStorage from) {
            return orientation == face;
        }
    }

    private static final double MAX_POWER = 40;
    private static final double MAX_TRANSFERED = 4;
    private static final double ACTIVATION_POWER = 2;
    private static final long LOSS_DELAY = 200;
    private static final double LOSS_RATE = 2;

    public TileEngineWood() {
        super(MAX_POWER, MAX_TRANSFERED, ACTIVATION_POWER, LOSS_DELAY, LOSS_RATE);
        externalStorage = new SidedMjExternalStorage(MAX_TRANSFERED);
        externalStorage.setInternalStorage(internalStorage);
    }

    // private boolean hasSent = false;

    @Override
    public String getResourcePrefix() {
        return "buildcraftcore:textures/blocks/engine/wood";
    }

    @Override
    public ResourceLocation getTrunkTexture(EnumEnergyStage stage) {
        return super.getTrunkTexture(stage == EnumEnergyStage.RED && progress < 0.5 ? EnumEnergyStage.YELLOW : stage);
    }

    @Override
    protected EnumEnergyStage computeEnergyStage() {
        double energyLevel = getEnergyPercentage();
        if (energyLevel < 0.33f) {
            return EnumEnergyStage.BLUE;
        } else if (energyLevel < 0.66f) {
            return EnumEnergyStage.GREEN;
        } else if (energyLevel < 0.75f) {
            return EnumEnergyStage.YELLOW;
        } else {
            return EnumEnergyStage.RED;
        }
    }

    @Override
    public float getPistonSpeed() {
        if (!worldObj.isRemote) {
            return Math.max(0.08f * getHeatLevel(), 0.01f);
        }

        switch (getEnergyStage()) {
            case GREEN:
                return 0.02F;
            case YELLOW:
                return 0.04F;
            case RED:
                return 0.08F;
            default:
                return 0.01F;
        }
    }

    @Override
    public void update() {
        super.update();

        if (isRedstonePowered) {
            if (worldObj.getTotalWorldTime() % 16 == 0) {
                internalStorage.insertPower(getWorld(), 1, false);
            }
        }
    }

    @Override
    public ConnectOverride overridePipeConnection(EnumPipeType type, EnumFacing with) {
        return ConnectOverride.DISCONNECT;
    }

    @Override
    public boolean isBurning() {
        return isRedstonePowered;
    }

    // @Override
    // public int getMaxEnergy() {
    // return 1000;
    // }
    //
    // @Override
    // public int calculateCurrentOutput() {
    // return 10;
    // }
    //
    // @Override
    // public int maxEnergyExtracted() {
    // return 10;
    // }

    // // TODO: HACK
    // @Override
    // public boolean canConnectEnergy(EnumFacing from) {
    // return false;
    // }
    //
    // @Override
    // protected boolean canSendPowerTo(TileEntity tile, IMjExternalStorage storage) {
    // if (!(storage instanceof IRedstoneEngineReceiver)) {
    // return false;
    // }
    // IRedstoneEngineReceiver reciever = (IRedstoneEngineReceiver) storage;
    // return reciever.canConnectRedstoneEngine(orientation);
    // }
    //
    // @Override
    // protected void sendPower() {
    // if (progressPart == 2 && !hasSent) {
    // hasSent = true;
    //
    // TileEntity tile = getTile(orientation);
    //
    // if (tile instanceof IRedstoneEngineReceiver && ((IRedstoneEngineReceiver)
    // tile).canConnectRedstoneEngine(orientation.getOpposite())) {
    // super.sendPower();
    // } else {
    // this.energy = 0;
    // }
    // } else if (progressPart != 2) {
    // hasSent = false;
    // }
    // }
}
