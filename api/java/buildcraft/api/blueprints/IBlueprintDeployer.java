package buildcraft.api.blueprints;

import java.io.File;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IBlueprintDeployer {
    void deployBlueprint(World world, BlockPos pos, EnumFacing dir, File file);

    void deployBlueprintFromFileStream(World world, BlockPos pos, EnumFacing dir, byte[] data);

}
