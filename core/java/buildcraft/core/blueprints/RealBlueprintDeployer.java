/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.blueprints;

import java.io.File;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import buildcraft.api.blueprints.BlueprintDeployer;
import buildcraft.core.lib.utils.NBTUtils;
import buildcraft.core.lib.utils.Utils;

public class RealBlueprintDeployer extends BlueprintDeployer {

    @Override
    public void deployBlueprint(World world, BlockPos pos, EnumFacing dir, File file) {
        Blueprint blueprint = (Blueprint) BlueprintBase.loadBluePrint(LibraryDatabase.load(file));
        readyBlueprint(blueprint);
        deployBlueprint(world, pos, dir, blueprint);
    }

    @Override
    public void deployBlueprintFromFileStream(World world, BlockPos pos, EnumFacing dir, byte[] data) {
        Blueprint blueprint = (Blueprint) BlueprintBase.loadBluePrint(NBTUtils.load(data));
        readyBlueprint(blueprint);
        deployBlueprint(world, pos, dir, blueprint);
    }

    private void readyBlueprint(Blueprint bpt) {
        bpt.id = new LibraryId();
        bpt.id.extension = "bpt";
    }

    public void deployBlueprint(World world, BlockPos pos, EnumFacing dir, Blueprint bpt) {
        BptContext context = bpt.getContext(world, bpt.getBoxForPos(pos));

        if (bpt.rotate) {
            if (dir == EnumFacing.EAST) {
                // Do nothing
            } else if (dir == EnumFacing.SOUTH) {
                bpt.rotateLeft(context);
            } else if (dir == EnumFacing.WEST) {
                bpt.rotateLeft(context);
                bpt.rotateLeft(context);
            } else if (dir == EnumFacing.NORTH) {
                bpt.rotateLeft(context);
                bpt.rotateLeft(context);
                bpt.rotateLeft(context);
            }
        }

        Vec3 transform = Utils.convert(pos).subtract(bpt.anchorX, bpt.anchorY, bpt.anchorZ);

        bpt.translateToWorld(transform);

        new BptBuilderBlueprint(bpt, world, pos).deploy();
    }
}
