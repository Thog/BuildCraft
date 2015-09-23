package buildcraft.core.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import buildcraft.api.enums.EnumDecoratedType;
import buildcraft.core.lib.block.BlockBuildCraftBase;

public class BlockDecoration extends BlockBuildCraftBase {
    public BlockDecoration() {
        super(Material.iron, DECORATED_TYPE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumDecoratedType type : EnumDecoratedType.values()) {
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return DECORATED_TYPE.getValue(state).ordinal();
    }

}
