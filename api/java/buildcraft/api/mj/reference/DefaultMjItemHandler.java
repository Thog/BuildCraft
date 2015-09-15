package buildcraft.api.mj.reference;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import buildcraft.api.enums.EnumInventoryDirection;
import buildcraft.api.mj.IMjItemHandler;

public class DefaultMjItemHandler implements IMjItemHandler {
    public static class MjItemPower {
        public double power;

        public void readFromNBT(ItemStack stack) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                power = 0;
            } else {
                power = tag.getDouble("mjpower");
            }
        }

        public void writeToNBT(ItemStack stack) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                tag.setDouble("mjpower", power);
                stack.setTagCompound(tag);
            } else {
                tag.setDouble("mjpower", power);
            }
        }
    }

    private final double maxPower;

    public DefaultMjItemHandler(double maxPower) {
        this.maxPower = maxPower;
    }

    @Override
    public double insertPower(ItemStack stack, EnumInventoryDirection flowDirection, double mj, boolean simulate) {
        if (mj < 0) {// Not a way to extract power
            return mj;
        }
        MjItemPower power = new MjItemPower();
        power.readFromNBT(stack);

        if (power.power == maxPower) {
            return mj;
        }
        double space = maxPower - power.power;
        double accepted = Math.min(space, mj);
        double excess = mj - accepted;

        if (!simulate) {
            power.power += accepted;
            power.writeToNBT(stack);
        }
        return excess;
    }

    @Override
    public double extractPower(ItemStack stack, EnumInventoryDirection flowDirection, double min, double max, boolean simulate) {
        if (min < 0 || max < 0) {// Not a way to insert power
            return 0;
        }
        MjItemPower power = new MjItemPower();
        power.readFromNBT(stack);

        if (power.power < min) {
            return 0;
        }
        double toTake = Math.min(max, power.power);
        if (!simulate) {
            power.power -= toTake;
            power.writeToNBT(stack);
        }
        return toTake;
    }
}
