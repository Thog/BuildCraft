package buildcraft.core.guide.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.enums.EnumEngineType;
import buildcraft.core.lib.engines.BlockEngineBase;

public class EngineBlockMapper implements IBlockGuidePageMapper {
    @Override
    public String getFor(World world, BlockPos pos, IBlockState state) {
        EnumEngineType type = BlockEngineBase.ENGINE_TYPE.getValue(state);
        return "engine_" + type.getLowercaseName();
    }

    @Override
    public List<String> getAllPossiblePages() {
        List<String> list = Lists.newArrayList();
        for (EnumEngineType type : EnumEngineType.values()) {
            list.add("engine_" + type.getLowercaseName());
        }
        return list;
    }
}
