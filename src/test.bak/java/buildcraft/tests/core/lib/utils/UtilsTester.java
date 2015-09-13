package buildcraft.tests.core.lib.utils;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import buildcraft.core.lib.utils.Utils;

public class UtilsTester {
    @Test
    public void testMinMaxForFace() {
        // Unknown alphabet ᘈ (Uncicode 0x1608) is used for the minimum (Because its a small character)
        // Unknown alphabet 彚 (Unicode 0x5F5A) is used for maximum (Because it basically fills the area)
        int ᘈ = 0;
        int 彚 = 2;

        BlockPos min = new BlockPos(ᘈ, ᘈ, ᘈ);
        BlockPos max = new BlockPos(彚, 彚, 彚);

        BlockPos downMin = Utils.getMinForFace(EnumFacing.DOWN, min, max);
        Assert.assertEquals(new BlockPos(ᘈ, ᘈ, ᘈ), downMin);

        BlockPos upMin = Utils.getMinForFace(EnumFacing.UP, min, max);
        Assert.assertEquals(new BlockPos(ᘈ, 彚, ᘈ), upMin);

        BlockPos eastMax = Utils.getMaxForFace(EnumFacing.EAST, min, max);
        Assert.assertEquals(new BlockPos(彚, 彚, 彚), eastMax);

        BlockPos westMax = Utils.getMaxForFace(EnumFacing.WEST, min, max);
        Assert.assertEquals(new BlockPos(ᘈ, 彚, 彚), westMax);
    }

    @Test
    public void testAllInAreaTiny() {
        BlockPos min = new BlockPos(0, 0, 0);
        BlockPos max = new BlockPos(0, 0, 0);

        Set<BlockPos> positions = Sets.newHashSet();
        positions.add(new BlockPos(0, 0, 0));

        for (BlockPos pos : Utils.allInBoxIncludingCorners(min, max)) {
            if (positions.contains(pos)) {
                positions.remove(pos);
            } else {
                Assert.fail("Did not contain " + pos);
            }
        }

        if (positions.size() > 0) {
            for (BlockPos pos : positions) {
                System.out.println(" - Contained " + pos);
            }
            Assert.fail("Contianed more block positions!");
        }
    }

    @Test
    public void testAllInAreaLarge() {
        BlockPos min = new BlockPos(0, 0, 0);
        BlockPos max = new BlockPos(10, 0, 9);

        Set<BlockPos> positions = Sets.newHashSet();
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 1; y++) {
                for (int z = 0; z < 10; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        positions.add(new BlockPos(0, 0, 0));

        for (BlockPos pos : Utils.allInBoxIncludingCorners(min, max)) {
            if (positions.contains(pos)) {
                positions.remove(pos);
            } else {
                Assert.fail("Did not contain " + pos);
            }
        }

        if (positions.size() > 0) {
            for (BlockPos pos : positions) {
                System.out.println(" - Contained " + pos);
            }
            Assert.fail("Contianed more block positions!");
        }
    }
}
