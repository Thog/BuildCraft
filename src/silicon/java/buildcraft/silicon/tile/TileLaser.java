/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.silicon.tile;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.EnumMjDeviceType;
import buildcraft.api.mj.EnumMjPowerType;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.IMjLaserStorage;
import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.tiles.IControllable;
import buildcraft.api.tiles.IHasWork;
import buildcraft.core.Box;
import buildcraft.core.EntityLaser;
import buildcraft.core.LaserData;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.utils.Utils;

import io.netty.buffer.ByteBuf;

public class TileLaser extends TileBuildCraft implements IHasWork, IControllable, IMjHandler {

    private static final float LASER_OFFSET = 2.0F / 16.0F;
    private static final short POWER_AVERAGING = 100;

    public LaserData laser = new LaserData();

    private final SafeTimeTracker laserTickTracker = new SafeTimeTracker(10);
    private final SafeTimeTracker searchTracker = new SafeTimeTracker(100, 100);
    private final SafeTimeTracker networkTracker = new SafeTimeTracker(20, 3);
    private int powerIndex = 0;

    private short powerAverage = 0;
    private final short[] power = new short[POWER_AVERAGING];

    private BlockPos targetPos;
    private IMjLaserStorage laserTarget;

    private final DefaultMjExternalStorage externalStorage;
    private final DefaultMjExternalStorage externalLaserStorage;
    private final DefaultMjInternalStorage internalStorage;

    public TileLaser() {
        super();
        internalStorage = new DefaultMjInternalStorage(1000, 25, 400, 2);

        externalStorage = new DefaultMjExternalStorage(EnumMjDeviceType.MACHINE, 5);
        externalStorage.setInternalStorage(internalStorage);

        externalLaserStorage = new DefaultMjExternalStorage(EnumMjDeviceType.ENGINE, EnumMjPowerType.LASER, 0);
        externalLaserStorage.setInternalStorage(internalStorage);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (laser == null) {
            laser = new LaserData();
        }

        laser.isVisible = false;
        laser.head = Utils.convertMiddle(getPos());
        laser.tail = Utils.convertMiddle(getPos());
        laser.isGlowing = true;
    }

    @Override
    public void update() {
        super.update();

        laser.iterateTexture();

        if (worldObj.isRemote) {
            return;
        }

        // If a gate disabled us, remove laser and do nothing.
        if (mode == IControllable.Mode.Off) {
            removeLaser();
            return;
        }

        // Check for any available tables at a regular basis
        if (canFindTable()) {
            findTable();
        }

        // If we still don't have a valid table or the existing has
        // become invalid, we disable the laser and do nothing.
        if (!isValidTable()) {
            removeLaser();
            return;
        }

        // Disable the laser and do nothing if no energy is available.
        if (!internalStorage.hasActivated()) {
            removeLaser();
            return;
        }

        // We have a table and can work, so we create a laser if
        // necessary.
        laser.isVisible = true;

        // We have a laser and may update it
        if (laser != null && canUpdateLaser()) {
            updateLaser();
        }

        // Consume power and transfer it to the table.
        double localPower = internalStorage.extractPower(getWorld(), 0, getMaxPowerSent(), false);
        laserTarget.insertPower(getWorld(), laserTarget.getTargetFace().getOpposite(), externalLaserStorage, localPower, false);

        if (laser != null) {
            pushPower(localPower);
        }

        onPowerSent(localPower);

        sendNetworkUpdate();
    }

    protected int getMaxPowerSent() {
        return 40;
    }

    protected void onPowerSent(double power) {}

    protected boolean canFindTable() {
        return searchTracker.markTimeIfDelay(worldObj);
    }

    protected boolean canUpdateLaser() {
        return laserTickTracker.markTimeIfDelay(worldObj);
    }

    protected boolean isValidTable() {
        if (laserTarget == null || !laserTarget.canCurrentlyRecievePower()) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    protected void findTable() {
        int meta = getBlockMetadata();

        BlockPos min = getPos().add(-5, -5, -5);
        BlockPos max = getPos().add(5, 5, 5);

        EnumFacing facing = EnumFacing.getFront(meta);
        if (facing.getAxisDirection() == AxisDirection.NEGATIVE) {
            max = max.offset(facing, 5);
        } else {
            min = min.offset(facing, -5);
        }

        List<IMjLaserStorage> targets = Lists.newLinkedList();
        List<BlockPos> positions = Lists.newArrayList();

        for (BlockPos pos : (Iterable<BlockPos>) BlockPos.getAllInBox(min, max)) {
            TileEntity tile = worldObj.getTileEntity(pos);
            if (tile == null || !(tile instanceof IMjHandler)) {
                continue;
            }
            IMjExternalStorage store = ((IMjHandler) tile).getMjStorage();
            if (store instanceof IMjLaserStorage) {
                IMjLaserStorage laser = (IMjLaserStorage) store;
                if (laser.canCurrentlyRecievePower()) {
                    targets.add(laser);
                    positions.add(pos);
                }
            }
        }

        if (targets.isEmpty()) {
            return;
        }

        int index = worldObj.rand.nextInt(targets.size());
        laserTarget = targets.get(index);
        targetPos = positions.get(index);
    }

    protected void updateLaser() {

        int meta = getBlockMetadata();
        double px = 0, py = 0, pz = 0;

        switch (EnumFacing.getFront(meta)) {

            case WEST:
                px = -LASER_OFFSET;
                break;
            case EAST:
                px = LASER_OFFSET;
                break;
            case DOWN:
                py = -LASER_OFFSET;
                break;
            case UP:
                py = LASER_OFFSET;
                break;
            case NORTH:
                pz = -LASER_OFFSET;
                break;
            case SOUTH:
            default:
                pz = LASER_OFFSET;
                break;
        }

        Vec3 head = Utils.convertMiddle(getPos()).addVector(px, py, pz);

        Vec3 tail = Utils.convert(targetPos).addVector(0.475 + (worldObj.rand.nextDouble() - 0.5) / 5d, 9 / 16d, 0.475 + (worldObj.rand.nextDouble()
            - 0.5) / 5d);

        laser.head = head;
        laser.tail = tail;

        if (!laser.isVisible) {
            laser.isVisible = true;
        }
    }

    protected void removeLaser() {
        if (powerAverage > 0) {
            pushPower(0);
        }
        if (laser.isVisible) {
            laser.isVisible = false;
            // force sending the network update even if the network tracker
            // refuses.
            super.sendNetworkUpdate();
        }
    }

    @Override
    public void sendNetworkUpdate() {
        if (networkTracker.markTimeIfDelay(worldObj)) {
            super.sendNetworkUpdate();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
    }

    @Override
    public void readData(ByteBuf stream) {
        laser = new LaserData();
        laser.readData(stream);
        powerAverage = stream.readShort();
    }

    @Override
    public void writeData(ByteBuf stream) {
        laser.writeData(stream);
        stream.writeShort(powerAverage);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        removeLaser();
    }

    @Override
    public boolean hasWork() {
        return isValidTable();
    }

    private void pushPower(double received) {
        powerAverage -= power[powerIndex];
        powerAverage += received;
        power[powerIndex] = (short) received;
        powerIndex++;

        if (powerIndex == power.length) {
            powerIndex = 0;
        }
    }

    public ResourceLocation getTexture() {
        double avg = powerAverage / POWER_AVERAGING;

        if (avg <= 10.0) {
            return EntityLaser.LASER_RED;
        } else if (avg <= 20.0) {
            return EntityLaser.LASER_YELLOW;
        } else if (avg <= 30.0) {
            return EntityLaser.LASER_GREEN;
        } else {
            return EntityLaser.LASER_BLUE;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new Box(this).extendToEncompass(laser.tail).getBoundingBox();
    }

    @Override
    public boolean acceptsControlMode(Mode mode) {
        return mode == IControllable.Mode.On || mode == IControllable.Mode.Off;
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }
}
