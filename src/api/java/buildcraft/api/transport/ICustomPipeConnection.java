package buildcraft.api.transport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface ICustomPipeConnection {
    /** @return How long the connecting pipe should extend for. */
    public float getExtension(World world, BlockPos pos, EnumFacing face, IBlockState state);
}
