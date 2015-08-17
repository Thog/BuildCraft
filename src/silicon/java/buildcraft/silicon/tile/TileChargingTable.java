package buildcraft.silicon.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import buildcraft.api.enums.EnumInventoryDirection;
import buildcraft.api.mj.IMjItemHandler;
import buildcraft.api.tiles.IHasWork;
import buildcraft.core.lib.utils.StringUtils;

public class TileChargingTable extends TileLaserTableBase implements IHasWork {
    @Override
    public void update() {
        super.update();

        if (worldObj.isRemote) {
            return;
        }

        if (internalStorage.currentPower() > 0) {
            if (getRequiredPower() > 0) {
                ItemStack stack = this.getStackInSlot(0);
                IMjItemHandler itemHandler = (IMjItemHandler) stack.getItem();
                double power = internalStorage.extractPower(getWorld(), 0, 10, false);
                double excess = itemHandler.insertPower(stack, EnumInventoryDirection.UNKNOWN, power, false);
                internalStorage.insertPower(getWorld(), excess, false);
                this.setInventorySlotContents(0, stack);
            } else {
                internalStorage.extractPower(getWorld(), 0, 10, false);
            }
        }
    }

    @Override
    public double getRequiredPower() {
        ItemStack stack = this.getStackInSlot(0);
        if (stack != null && stack.getItem() != null && stack.getItem() instanceof IMjItemHandler) {
            IMjItemHandler item = (IMjItemHandler) stack.getItem();
            double excess = item.insertPower(stack, EnumInventoryDirection.UNKNOWN, 1000, true);
            return 1000 - excess;
            // Returns anywhere between 1000 and 0 even if the item has more room than 100 MJ
        }
        return 0;
    }

    @Override
    public boolean hasWork() {
        return getRequiredPower() > 0;
    }

    @Override
    public boolean canCraft() {
        return hasWork();
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public String getInventoryName() {
        return StringUtils.localize("tile.chargingTableBlock.name");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0 && stack != null && stack.getItem() != null && stack.getItem() instanceof IMjItemHandler;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}
}
