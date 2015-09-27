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

import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;

public class DefaultMjExternalStorageTester {
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

    public DefaultMjExternalStorage makeNewTest(EnumMjDevice device, EnumMjPower power) {
        DefaultMjInternalStorage internal = new DefaultMjInternalStorage(10, 1, 10, 1);
        // Just so we can test some values
        internal.power = 1;
        DefaultMjExternalStorage external = new DefaultMjExternalStorage(device, power, 2);
        external.setInternalStorage(internal);
        return external;
    }

    @Test
    public void testSimpleInternals() {
        World world = makeWorld();

        DefaultMjExternalStorage transportTo = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage transportFrom = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);

        double excess = transportTo.insertPower(world, null, transportFrom, 1, false);
        Assert.assertEquals(0, excess, 0.1);

        // It only contains 2 Mj
        double mj = transportTo.extractPower(world, null, transportFrom, 4, 4, false);
        Assert.assertEquals(0, mj, 0.1);

        mj = transportTo.extractPower(world, null, transportFrom, 0, 1, false);
        Assert.assertEquals(1, mj, 0.1);

        mj = 5;

        excess = transportTo.insertPower(world, null, transportFrom, mj, false);
        Assert.assertEquals(3, excess, 0.1);// It limits it to 2 Mj per flow, so 5-2 should be 3 left over
    }

    @Test
    public void testDeviceDifferencesEngine() {
        World world = makeWorld();

        DefaultMjExternalStorage engine = makeNewTest(EnumMjDevice.ENGINE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage transport = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage storage = makeNewTest(EnumMjDevice.STORAGE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage machine = makeNewTest(EnumMjDevice.MACHINE, EnumMjPower.NORMAL);

        // Test engine -> everything

        // Engines can insert into anything EXCEPT storage
        double mj = engine.extractPower(world, null, engine, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = engine.extractPower(world, null, transport, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = engine.extractPower(world, null, storage, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = engine.extractPower(world, null, machine, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        // Test everything -> engine

        // Engines can only ACCEPT from other engines

        double excess = engine.insertPower(world, null, engine, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = engine.insertPower(world, null, transport, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = engine.insertPower(world, null, storage, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = engine.insertPower(world, null, machine, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testDeviceDifferencesTransport() {
        World world = makeWorld();

        DefaultMjExternalStorage engine = makeNewTest(EnumMjDevice.ENGINE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage transport = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage storage = makeNewTest(EnumMjDevice.STORAGE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage machine = makeNewTest(EnumMjDevice.MACHINE, EnumMjPower.NORMAL);

        // Test transport -> everything

        // Transport can insert into anything EXCEPT engines
        double mj = transport.extractPower(world, null, engine, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = transport.extractPower(world, null, transport, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = transport.extractPower(world, null, storage, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = transport.extractPower(world, null, machine, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        // Test everything -> transport

        // Transport can only NOT ACCEPT from machines

        double excess = transport.insertPower(world, null, engine, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = transport.insertPower(world, null, transport, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = transport.insertPower(world, null, storage, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = transport.insertPower(world, null, machine, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testDeviceDifferencesStorage() {
        World world = makeWorld();

        DefaultMjExternalStorage engine = makeNewTest(EnumMjDevice.ENGINE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage transport = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage storage = makeNewTest(EnumMjDevice.STORAGE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage machine = makeNewTest(EnumMjDevice.MACHINE, EnumMjPower.NORMAL);

        // Test storage -> everything

        // Storage can insert into transport
        double mj = storage.extractPower(world, null, engine, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = storage.extractPower(world, null, transport, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = storage.extractPower(world, null, storage, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = storage.extractPower(world, null, machine, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        // Test everything -> storage

        // Storage can only accept from transport

        double excess = storage.insertPower(world, null, engine, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = storage.insertPower(world, null, transport, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = storage.insertPower(world, null, storage, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = storage.insertPower(world, null, machine, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testDeviceDifferencesMachine() {
        World world = makeWorld();

        DefaultMjExternalStorage engine = makeNewTest(EnumMjDevice.ENGINE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage transport = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage storage = makeNewTest(EnumMjDevice.STORAGE, EnumMjPower.NORMAL);
        DefaultMjExternalStorage machine = makeNewTest(EnumMjDevice.MACHINE, EnumMjPower.NORMAL);

        // Test machine -> everything

        // Machines never allow any power to get out
        double mj = machine.extractPower(world, null, engine, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = machine.extractPower(world, null, transport, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = machine.extractPower(world, null, storage, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = machine.extractPower(world, null, machine, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        // Test everything -> machine

        // Machine can only accept from transport or engines

        double excess = machine.insertPower(world, null, engine, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = machine.insertPower(world, null, transport, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = machine.insertPower(world, null, storage, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = machine.insertPower(world, null, machine, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testPowerDifferencesNone() {
        World world = makeWorld();

        DefaultMjExternalStorage none = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NONE);
        DefaultMjExternalStorage redstone = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.REDSTONE);
        DefaultMjExternalStorage normal = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage laser = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.LASER);

        // test none -> everything

        double mj = none.extractPower(world, null, none, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = none.extractPower(world, null, redstone, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = none.extractPower(world, null, normal, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = none.extractPower(world, null, laser, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        // Test everything -> none

        double excess = none.insertPower(world, null, none, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = none.insertPower(world, null, redstone, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = none.insertPower(world, null, normal, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = none.insertPower(world, null, laser, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testPowerDifferencesRedstone() {
        World world = makeWorld();

        DefaultMjExternalStorage none = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NONE);
        DefaultMjExternalStorage redstone = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.REDSTONE);
        DefaultMjExternalStorage normal = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage laser = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.LASER);

        // test redstone -> everything

        double mj = redstone.extractPower(world, null, none, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = redstone.extractPower(world, null, redstone, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = redstone.extractPower(world, null, normal, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = redstone.extractPower(world, null, laser, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        // Test everything -> redstone

        double excess = redstone.insertPower(world, null, none, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = redstone.insertPower(world, null, redstone, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = redstone.insertPower(world, null, normal, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = redstone.insertPower(world, null, laser, 1, true);
        Assert.assertEquals(1, excess, 0.1);
    }

    @Test
    public void testPowerDifferencesNormal() {
        World world = makeWorld();

        DefaultMjExternalStorage none = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NONE);
        DefaultMjExternalStorage redstone = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.REDSTONE);
        DefaultMjExternalStorage normal = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage laser = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.LASER);

        // test normal -> everything

        double mj = normal.extractPower(world, null, none, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = normal.extractPower(world, null, redstone, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = normal.extractPower(world, null, normal, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = normal.extractPower(world, null, laser, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        // Test everything -> normal

        double excess = normal.insertPower(world, null, none, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = normal.insertPower(world, null, redstone, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = normal.insertPower(world, null, normal, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = normal.insertPower(world, null, laser, 1, true);
        Assert.assertEquals(0, excess, 0.1);
    }

    @Test
    public void testPowerDifferencesLaser() {
        World world = makeWorld();

        DefaultMjExternalStorage none = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NONE);
        DefaultMjExternalStorage redstone = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.REDSTONE);
        DefaultMjExternalStorage normal = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.NORMAL);
        DefaultMjExternalStorage laser = makeNewTest(EnumMjDevice.TRANSPORT, EnumMjPower.LASER);

        // test laser -> everything

        double mj = laser.extractPower(world, null, none, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = laser.extractPower(world, null, redstone, 0, 1, true);
        Assert.assertEquals(0, mj, 0.1);

        mj = laser.extractPower(world, null, normal, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        mj = laser.extractPower(world, null, laser, 0, 1, true);
        Assert.assertEquals(1, mj, 0.1);

        // Test everything -> none

        double excess = laser.insertPower(world, null, none, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = laser.insertPower(world, null, redstone, 1, true);
        Assert.assertEquals(1, excess, 0.1);

        excess = laser.insertPower(world, null, normal, 1, true);
        Assert.assertEquals(0, excess, 0.1);

        excess = laser.insertPower(world, null, laser, 1, true);
        Assert.assertEquals(0, excess, 0.1);
    }

}
