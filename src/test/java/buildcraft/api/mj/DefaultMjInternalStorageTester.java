package buildcraft.api.mj;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

import buildcraft.api.mj.reference.DefaultMjInternalStorage;

public class DefaultMjInternalStorageTester {
    public World makeWorld() {
        WorldInfo info = new WorldInfo(new WorldSettings(0, GameType.SPECTATOR, false, false, WorldType.FLAT), "a name");
        WorldProvider provider = new WorldProviderSurface();
        World world = new World(null, info, provider, null, false) {
            @Override
            protected IChunkProvider createChunkProvider() {
                return null;
            }

            @Override
            protected int getRenderDistanceChunks() {
                return 0;
            }
        };

        return world;
    }

    public DefaultMjInternalStorage makeNewTest() {
        return new DefaultMjInternalStorage(10, 1, 10, 1);
    }

    @Test
    public void testSimple() {
        World world = makeWorld();
        DefaultMjInternalStorage test = makeNewTest();
        // Make sure that 2 mj was left over from trying to insert 12 to 0/10
        Assert.assertEquals(2, test.insertPower(world, 12, false), 0.1);
        // Make sure that 12 mj was left over after trying to insert 12 to 10/10
        Assert.assertEquals(12, test.insertPower(world, 12, false), 0.1);
        // Make sure that 0 mj was extracted when trying to extract between 11 and 12 from 10/10
        Assert.assertEquals(0, test.extractPower(world, 11, 12, false), 0.1);
        // Make sure that 10 mj was extracted when trying to extract between 0 and 12 from 10/10
        Assert.assertEquals(10, test.extractPower(world, 0, 12, false), 0.1);
    }

    @Test
    public void testTicks() {
        World world = makeWorld();
        DefaultMjInternalStorage test = makeNewTest();
        test.insertPower(world, 10, false);
        long worldTicks = 0;
        for (int i = 0; i < 10; i++) {
            worldTicks++;
            world.getWorldInfo().incrementTotalWorldTime(worldTicks);
            test.tick(world);
            Assert.assertEquals(10, test.currentPower(), 0.1);
        }
        double lastTime = test.currentPower();
        for (int i = 0; i < 10; i++) {
            worldTicks++;
            world.getWorldInfo().incrementTotalWorldTime(worldTicks);
            test.tick(world);
            Assert.assertEquals(lastTime - 1, test.currentPower(), 0.1);
            lastTime = test.currentPower();
        }
    }

    @Test
    public void testSuction() {
        World world = makeWorld();
        DefaultMjInternalStorage test = makeNewTest();
        Assert.assertEquals(1, test.getSuction(), 0.1);
        test.insertPower(world, 5, false);
        Assert.assertEquals(0.5, test.getSuction(), 0.1);
        test.insertPower(world, 5, false);
        Assert.assertEquals(0, test.getSuction(), 0.1);
    }
}
