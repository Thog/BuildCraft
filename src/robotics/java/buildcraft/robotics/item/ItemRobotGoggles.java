package buildcraft.robotics.item;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import buildcraft.core.BCCreativeTab;
import buildcraft.core.lib.utils.Utils;
import buildcraft.robotics.pathfinding.ServerNetworkManager;
import buildcraft.robotics.pathfinding.WorldNetworkManager;

public class ItemRobotGoggles extends ItemArmor implements ISpecialArmor {
    public ItemRobotGoggles() {
        super(ArmorMaterial.CHAIN, 0, 0);
        setCreativeTab(BCCreativeTab.get("main"));
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ArmorProperties(0, 0, 0);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        // Never damaged
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return null;// TODO!
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            BlockPos here = Utils.getPos(player);
            BlockPos other;
            int tries = 0;
            while (tries < 10) {
                int offX = new Random().nextInt(40) + 20;
                int offY = new Random().nextInt(20) - 10;
                int offZ = new Random().nextInt(40) + 20;
                other = here.add(offX, offY, offZ);
                if (world.isAirBlock(here)) {
                    ServerNetworkManager manager = (ServerNetworkManager) WorldNetworkManager.getForWorld(world);
                    manager.makePath(here, other);
                }
                break;
            }
            tries++;
        }
        return stack;
    }
}
