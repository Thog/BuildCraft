package buildcraft.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import buildcraft.core.BuildCraftCore;
import buildcraft.core.EnumGui;
import buildcraft.core.lib.items.ItemBuildCraft;

public class ItemGuide extends ItemBuildCraft {
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            return stack;
        }
        player.openGui(BuildCraftCore.instance, EnumGui.GUIDE.ID, world, 0, 0, 0);
        return stack;
    }
}
