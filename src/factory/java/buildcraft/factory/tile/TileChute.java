/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.factory.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjExternalStorage.IConnectionLimiter;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.transport.IInjectable;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.inventory.ITransactor;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.inventory.Transactor;

public class TileChute extends TileBuildCraft implements IInventory, IMjHandler {

    private final SimpleInventory inventory = new SimpleInventory(4, "Chute", 64);
    private boolean isEmpty;

    private final DefaultMjExternalStorage externalStorage;
    private final DefaultMjInternalStorage internalStorage;

    public TileChute() {
        externalStorage = new DefaultMjExternalStorage(EnumMjDevice.MACHINE, EnumMjPower.REDSTONE, 1.0);
        externalStorage.addLimiter(new IConnectionLimiter() {
            @Override
            public boolean allowConnection(World world, EnumFacing flow, IMjExternalStorage thisOne, IMjExternalStorage other, boolean in) {
                return flow.getAxis() != Axis.Y;
            }
        });
        internalStorage = new DefaultMjInternalStorage(2.0, 1.0, 400, 0.1);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public void initialize() {
        inventory.addListener(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        // NBTTagCompound inventoryNbt = nbt;

        // if (nbt.hasKey("inventory")) {
        // // to support pre 6.0 loading
        // inventoryNbt = nbt.getCompoundTag("inventory");
        // }
        // inventory.readFromNBT(inventoryNbt);
        inventory.readFromNBT(nbt);
        inventory.markDirty();

        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
        inventory.writeToNBT(nbt);
    }

    @Override
    public void update() {
        super.update();
        if (worldObj.isRemote) {
            return;
        }

        internalStorage.tick(getWorld());

        if (isEmpty || worldObj.getTotalWorldTime() % 2 != 0) {
            return;
        }

        TileEntity outputTile = getTile(EnumFacing.DOWN);

        ITransactor transactor = Transactor.getTransactorFor(outputTile);

        if (transactor == null) {
            if (outputTile instanceof IInjectable && internalStorage.currentPower() >= 1) {
                ItemStack stackToOutput = null;
                int internalSlot = 0;

                internalStorage.extractPower(getWorld(), 1, 1, false);

                for (; internalSlot < inventory.getSizeInventory(); internalSlot++) {
                    ItemStack stackInSlot = inventory.getStackInSlot(internalSlot);
                    if (stackInSlot == null || stackInSlot.stackSize == 0) {
                        continue;
                    }
                    stackToOutput = stackInSlot.copy();
                    stackToOutput.stackSize = 1;
                    break;
                }

                if (stackToOutput != null) {
                    int used = ((IInjectable) outputTile).injectItem(stackToOutput, true, EnumFacing.UP, null);
                    if (used > 0) {
                        decrStackSize(internalSlot, 1);
                    }
                }
            }

            return;
        }

        for (int internalSlot = 0; internalSlot < inventory.getSizeInventory(); internalSlot++) {
            ItemStack stackInSlot = inventory.getStackInSlot(internalSlot);
            if (stackInSlot == null || stackInSlot.stackSize == 0) {
                continue;
            }

            ItemStack clonedStack = stackInSlot.copy().splitStack(1);
            if (transactor.add(clonedStack, EnumFacing.UP, true).stackSize > 0) {
                inventory.decrStackSize(internalSlot, 1);
                return;
            }
        }
    }

    @Override
    public void markDirty() {
        isEmpty = true;

        for (int internalSlot = 0; internalSlot < inventory.getSizeInventory(); internalSlot++) {
            ItemStack stackInSlot = inventory.getStackInSlot(internalSlot);
            if (stackInSlot != null && stackInSlot.stackSize > 0) {
                isEmpty = false;
                return;
            }
        }
    }

    /** IInventory Implementation * */
    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        return inventory.getStackInSlot(slotId);
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack output = inventory.decrStackSize(slotId, count);
        return output;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotId) {
        ItemStack output = inventory.getStackInSlotOnClosing(slotId);
        return output;
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemStack) {
        inventory.setInventorySlotContents(slotId, itemStack);
    }

    @Override
    public String getInventoryName() {
        return inventory.getCommandSenderName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return worldObj.getTileEntity(pos) == this && entityPlayer.getDistanceSq(pos) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public String getOwner() {
        return super.getOwner();
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }
}
