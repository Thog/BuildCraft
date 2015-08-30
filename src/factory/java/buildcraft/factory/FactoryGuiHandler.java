/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.factory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import buildcraft.core.EnumGui;
import buildcraft.factory.gui.ContainerAutoWorkbench;
import buildcraft.factory.gui.ContainerChute;
import buildcraft.factory.gui.ContainerRefinery;
import buildcraft.factory.gui.GuiAutoCrafting;
import buildcraft.factory.gui.GuiChute;
import buildcraft.factory.gui.GuiRefinery;
import buildcraft.factory.tile.TileAutoWorkbench;
import buildcraft.factory.tile.TileChute;
import buildcraft.factory.tile.TileRefinery;

public class FactoryGuiHandler implements IGuiHandler {

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

        BlockPos pos = new BlockPos(x, y, z);

        if (world.isAirBlock(pos)) {
            return null;
        }

        TileEntity tile = world.getTileEntity(pos);

        switch (EnumGui.from(id)) {

            case AUTO_CRAFTING_TABLE:
                if (!(tile instanceof TileAutoWorkbench)) {
                    return null;
                } else {
                    return new GuiAutoCrafting(player.inventory, world, (TileAutoWorkbench) tile);
                }

            case REFINERY:
                if (!(tile instanceof TileRefinery)) {
                    return null;
                } else {
                    return new GuiRefinery(player.inventory, (TileRefinery) tile);
                }

            case CHUTE:
                if (!(tile instanceof TileChute)) {
                    return null;
                } else {
                    return new GuiChute(player.inventory, (TileChute) tile);
                }

            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

        BlockPos pos = new BlockPos(x, y, z);

        if (world.isAirBlock(pos)) {
            return null;
        }

        TileEntity tile = world.getTileEntity(pos);

        switch (EnumGui.from(id)) {

            case AUTO_CRAFTING_TABLE:
                if (!(tile instanceof TileAutoWorkbench)) {
                    return null;
                } else {
                    return new ContainerAutoWorkbench(player.inventory, (TileAutoWorkbench) tile);
                }

            case REFINERY:
                if (!(tile instanceof TileRefinery)) {
                    return null;
                } else {
                    return new ContainerRefinery(player.inventory, (TileRefinery) tile);
                }

            case CHUTE:
                if (!(tile instanceof TileChute)) {
                    return null;
                } else {
                    return new ContainerChute(player.inventory, (TileChute) tile);
                }

            default:
                return null;
        }
    }

}
