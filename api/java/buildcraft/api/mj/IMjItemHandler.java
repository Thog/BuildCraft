package buildcraft.api.mj;

import net.minecraft.item.ItemStack;

import buildcraft.api.enums.EnumInventoryDirection;

public interface IMjItemHandler {
    /** @param stack The item stack that is trying to have power extracted from
     * @param flowDirection The direction (In the players inventory) that power is trying to flow. Never null.
     * @param min The minimum amount of power that should be extracted
     * @param max The maximum amount of power that should be extracted
     * @param simulate If true, the item stack should be unaffected afterwards
     * @return The amount of power that was extracted. */
    double extractPower(ItemStack stack, EnumInventoryDirection flowDirection, double min, double max, boolean simulate);

    /** @param stack The item stack that is trying to have power extracted from
     * @param flowDirection The direction (In the players inventory) that power is trying to flow. Never null.
     * @param mj The amount of power that should be inserted
     * @param simulate If true, the item stack should be unaffected afterwards
     * @return The amount of power that was not inserted */
    double insertPower(ItemStack stack, EnumInventoryDirection flowDirection, double mj, boolean simulate);
}
