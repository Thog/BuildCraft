/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.silicon.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.mj.reference.DefaultMjLaserExternalStorage;
import buildcraft.api.tiles.IHasWork;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.utils.Average;

public abstract class TileLaserTableBase extends TileBuildCraft implements IInventory, IHasWork, IMjHandler {
    private static final AxisAlignedBB TARGET = new AxisAlignedBB(7 / 16d, 9 / 16d, 7 / 16d, 9 / 16d, 9 / 16d, 9 / 16d);

    private static final double MAX_POWER = 10000;
    private static final double MAX_TRANSFERED = 100;
    private static final long LOSS_DELAY = 4000;
    private static final double LOSS_RATE = 1;

    public double clientRequiredPower = 0;
    public double clientPower = 0;
    public double clientRecentPowerAverage;
    private int clientRawPower, clientRawRequired, clientRawRecent;
    protected SimpleInventory inv = new SimpleInventory(getSizeInventory(), "inv", 64);
    private Average recentEnergyAverageUtil = new Average(20);

    protected final DefaultMjLaserExternalStorage externalStorage;
    protected final DefaultMjInternalStorage internalStorage;

    public TileLaserTableBase() {
        externalStorage = new DefaultMjLaserExternalStorage(this, EnumMjDevice.MACHINE, MAX_TRANSFERED, EnumFacing.UP, TARGET) {
            @Override
            public boolean canCurrentlyRecievePower() {
                return requiresLaserEnergy();
            }
        };
        internalStorage = new DefaultMjInternalStorage(MAX_POWER, 0, LOSS_DELAY, LOSS_RATE);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public void update() {
        super.update();
        recentEnergyAverageUtil.tick();
    }

    public abstract double getRequiredPower();

    public int getProgressScaled(int ratio) {
        if (clientRequiredPower == 0) {
            return 0;
        } else if (clientPower >= clientRequiredPower) {
            return ratio;
        } else {
            return (int) (ratio * clientPower / clientRequiredPower);
        }
    }

    public double getRecentPowerAverage() {
        return clientRecentPowerAverage;
    }

    public abstract boolean canCraft();

    public boolean requiresLaserEnergy() {
        return canCraft() && internalStorage.currentPower() < getRequiredPower() * 5;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return inv.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(getPos()) == this && !isInvalid();
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        inv.writeToNBT(nbt, "inv");
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inv.readFromNBT(nbt, "inv");
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
    }

    public void getGUINetworkData(int id, int data) {

        switch (id) {
            case 0:
                clientRawRequired = (clientRawRequired & 0xFFFF0000) | (data & 0xFFFF);
                clientRequiredPower = Float.intBitsToFloat(clientRawRequired);
                break;
            case 1:
                clientRawRequired = (clientRawRequired & 0xFFFF) | ((data & 0xFFFF) << 16);
                clientRequiredPower = Float.intBitsToFloat(clientRawRequired);
                break;
            case 2:
                clientRawPower = (clientRawPower & 0xFFFF0000) | (data & 0xFFFF);
                clientPower = Float.intBitsToFloat(clientRawPower);
                break;
            case 3:
                clientRawPower = (clientRawPower & 0xFFFF) | ((data & 0xFFFF) << 16);
                clientPower = Float.intBitsToFloat(clientRawPower);
                break;
            case 4:
                clientRawRecent = clientRawRecent & 0xFFFF0000 | (data & 0xFFFF);
                clientRecentPowerAverage = Float.intBitsToFloat(clientRawRecent);
                break;
            case 5:
                clientRawRecent = (clientRawRecent & 0xFFFF) | ((data & 0xFFFF) << 16);
                clientRecentPowerAverage = Float.intBitsToFloat(clientRawRecent);
                break;
        }
    }

    public void sendGUINetworkData(Container container, ICrafting iCrafting) {
        int requiredEnergy = Float.floatToRawIntBits((float) getRequiredPower());
        int currentStored = Float.floatToRawIntBits((float) internalStorage.currentPower());
        int lRecentEnergy = (int) (recentEnergyAverageUtil.getAverage() * 100f);
        iCrafting.sendProgressBarUpdate(container, 0, requiredEnergy & 0xFFFF);
        iCrafting.sendProgressBarUpdate(container, 1, (requiredEnergy >>> 16) & 0xFFFF);
        iCrafting.sendProgressBarUpdate(container, 2, currentStored & 0xFFFF);
        iCrafting.sendProgressBarUpdate(container, 3, (currentStored >>> 16) & 0xFFFF);
        iCrafting.sendProgressBarUpdate(container, 4, lRecentEnergy & 0xFFFF);
        iCrafting.sendProgressBarUpdate(container, 5, (lRecentEnergy >>> 16) & 0xFFFF);
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }
}
