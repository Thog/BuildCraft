package buildcraft.transport.pipes;

import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.core.BCLog;
import buildcraft.api.core.ISerializable;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjConnection;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.core.lib.utils.Average;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

import io.netty.buffer.ByteBuf;

public abstract class PipePowerBase extends Pipe<PipeTransportPower>implements IMjExternalStorage, ISerializable {
    // TODO (PASS 0): refactor this into PipeTransportPower

    // TYPES:
    // WOOD --extract from engines
    // COBBLE --doesn't connect to STONE or QUARTZ
    // STONE --doesn't connect to COBBLE or QUARTZ
    // QUARTZ --doesn't connect to COBBLE or STONE
    // SANDSTONE --doesn't connect to machines
    // IRON --power only flows out one way
    // GOLD --not sure
    // CLAY --prioritises machines over pipes
    public static final double MAX_POWER = 1024;
    public static final double ACTIVATION = 0.1;
    public static final long LOSS_DELAY = 10;
    public static final double LOSS_RATE = 8;
    public static final double MIN_TRANSFER = 1;

    protected EnumMap<EnumFacing, DefaultMjInternalStorage> pipePartMap = Maps.newEnumMap(EnumFacing.class);
    protected EnumMap<EnumFacing, Average> pipePartDirections = Maps.newEnumMap(EnumFacing.class);

    public PipePowerBase(Item item) {
        super(new PipeTransportPower(), item);
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.put(face, new DefaultMjInternalStorage(MAX_POWER, ACTIVATION, LOSS_DELAY, LOSS_RATE));
            pipePartDirections.put(face, new Average(40));
        }
    }

    @Override
    public EnumMjDevice getDeviceType(EnumFacing side) {
        return EnumMjDevice.TRANSPORT;
    }

    @Override
    public EnumMjPower getPowerType(EnumFacing side) {
        return EnumMjPower.NORMAL;
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return 0;// You cannot extract power (directly) from pipes. Neither can pipes.
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        if (!(from instanceof Pipe<?>)) {
            BCLog.logger.info(from.getClass());
            return mj;// You cannot insert power (directly) to pipes -wooden pipes implement this separately for
                      // redstone engines
        }
        EnumFacing pipePart = flowDirection.getOpposite();
        DefaultMjInternalStorage storage = pipePartMap.get(pipePart);
        double excess = storage.insertPower(world, mj, simulate);
        double actual = mj - excess;
        pipePartDirections.get(pipePart).push(actual);
        return excess;
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        if (flowDirection == null) {
            // Get the suction of all the pipe parts
            double suction = 0;
            for (EnumFacing face : EnumFacing.values()) {
                suction += getSuction(world, face);
            }
            return suction / 6d;
        }
        IMjInternalStorage storage = pipePartMap.get(flowDirection.getOpposite());
        return storage.getSuction();
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {
        // NO-OP (Its handled seperately)
    }

    @Override
    public double currentPower(EnumFacing side) {
        if (side == null) {
            return 0;
        }
        return pipePartMap.get(side).currentPower();
    }

    @Override
    public double maxPower(EnumFacing side) {
        if (side == null) {
            return MAX_POWER;
        }
        return pipePartMap.get(side).maxPower();
    }

    @Override
    public void update() {
        super.update();
        if (container.getWorld().isRemote) {
            return;
        }
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.get(face).tick(getWorld());
            pipePartDirections.get(face).tick();
        }
        transferPower();
        // Update client info
        for (EnumFacing face : EnumFacing.values()) {
            int ord = face.ordinal();

            DefaultMjInternalStorage storage = pipePartMap.get(face);
            double filled = storage.currentPower() / storage.maxPower();
            double display = Math.sqrt(filled);
            display *= PipeTransportPower.POWER_STAGES;
            transport.displayPower[ord] = (byte) display;

            Average average = pipePartDirections.get(face);
            if (storage.currentPower() > 1e-2) {
                double flow = average.getAverage() / storage.currentPower() / 2;
                if (flow > 1) {
                    flow = 1;
                }
                // flow = Math.sqrt(flow);
                // System.out.println(average.getAverage() + ", " + filled + " -> " + flow);
                transport.displayFlow[ord] = (byte) flow;
            } else {
                transport.displayFlow[ord] = 0;
            }
        }
    }

    protected void transferPower() {
        // First, try to flow power around internally
        List<EnumFacing> directions = Lists.newArrayList();
        // Sort the directions in order of how much power has flown into them recently
        int numNeg = 0;
        int numMid = 0;
        int numPos = 0;
        double totalNegPower = 0;
        for (EnumFacing face : EnumFacing.values()) {
            int pos = 0;
            if (!container.isPipeConnected(face)) {
                continue;
            }
            double powerFlow = pipePartDirections.get(face).getAverage();
            DefaultMjInternalStorage pipe = pipePartMap.get(face);
            double power = pipe.currentPower() >= MIN_TRANSFER ? pipe.currentPower() / 2d : pipe.currentPower();
            /* if (powerFlow == 0) { numMid++; } else */ if (powerFlow <= 0) {
                numNeg++;
                totalNegPower += power;
            } else {
                numPos++;
            }
            for (EnumFacing inList : directions) {
                if (powerFlow > pipePartDirections.get(inList).getAverage()) {
                    pos++;
                } else {
                    break;
                }
            }
            directions.add(pos, face);
        }

        double heldPower = 0;
        for (DefaultMjInternalStorage store : pipePartMap.values()) {
            double toExtract = store.currentPower() >= MIN_TRANSFER ? store.currentPower() / 2d : store.currentPower();
            heldPower += store.extractPower(getWorld(), 0, toExtract, false);
        }
        if (numNeg > 0) {
            // if (totalNegPower > 1) {
            // Only put power into the negatives if there is enough power for it to be worth it
            for (int i = 0; i < numNeg; i++) {
                EnumFacing face = directions.get(i);
                // double powerFlow = pipePartDirections.get(face).getAverage();
                double mj = heldPower /* * powerFlow / totalNegPower */ / numNeg;
                heldPower -= mj;
                heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
            }
            // } else {

            // }
        }
        if (numMid > 0 && heldPower > 0) {
            double totalHeldPower = heldPower;
            for (int i = 0; i < numMid; i++) {
                EnumFacing face = directions.get(i);
                double mj = totalHeldPower / numMid;
                heldPower -= mj;
                heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
            }
        }
        if (numPos > 0 && heldPower > 0) {
            for (int i = 0; i < directions.size(); i++) {
                EnumFacing face = directions.get(i);
                double mj = heldPower / numPos;
                heldPower -= mj;
                heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
            }
        }
        if (heldPower != 0) {
            // if this ever happens, something went wrong with the above algorithm!
            // Like one of the pipes filled up completely. Then its probably ok.
            System.out.println(heldPower);
        }

        // Then transfer out
        for (EnumFacing face : EnumFacing.values()) {
            if (container.isPipeConnected(face)) {
                TileEntity tile = container.getWorld().getTileEntity(container.getPos().offset(face));
                if (tile == null || !(tile instanceof IMjHandler)) {
                    // Most of the time it SHOULD be a valid handler, but just check anyway
                    continue;
                }
                IMjExternalStorage external = ((IMjHandler) tile).getMjStorage();

                if (external instanceof IMjConnection) {
                    IMjConnection connection = (IMjConnection) external;
                    if (!connection.canConnectPower(face, this)) {
                        continue;
                    }
                }

                DefaultMjInternalStorage storage = pipePartMap.get(face);
                double suctionOut = external.getSuction(getWorld(), face);
                double suctionDifference = suctionOut - storage.getSuction() / EnumMjDevice.TRANSPORT.getSuctionDivisor();

                if (suctionDifference <= 0) {
                    if (external.getDeviceType(face.getOpposite()) != EnumMjDevice.MACHINE) {
                        continue;
                    } else {
                        suctionDifference = suctionOut;
                    }
                }
                suctionDifference += 0.3;

                double mj = storage.extractPower(getWorld(), 0, storage.currentPower() * suctionDifference / 2, false);
                double excess = external.insertPower(getWorld(), face, this, mj, false);
                pipePartDirections.get(face).push(excess - mj);
                storage.insertPower(getWorld(), excess, false);
            } else if (container.hasPipePluggable(face)) {
                // TODO
            }
        }
    }

    protected boolean isValidDestination(IMjExternalStorage storage, EnumFacing flowDir) {
        return storage.getDeviceType(flowDir.getOpposite()).acceptsPowerFrom(getDeviceType(null));
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTTagCompound storageTag = data.getCompoundTag("storage");
        NBTTagCompound averageTag = data.getCompoundTag("average");

        for (EnumFacing face : EnumFacing.values()) {

            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storage.readFromNBT(storageTag.getCompoundTag(face.name()));

            Average average = pipePartDirections.get(face);
            average.readFromNBT(averageTag.getCompoundTag(face.name()));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound storageTag = new NBTTagCompound();
        NBTTagCompound averageTag = new NBTTagCompound();

        for (EnumFacing face : EnumFacing.values()) {

            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storageTag.setTag(face.name(), storage.writeToNBT());

            Average average = pipePartDirections.get(face);
            averageTag.setTag(face.name(), average.writeToNBT());
        }
        data.setTag("storage", storageTag);
        data.setTag("average", averageTag);
    }

    public void writeData(ByteBuf data) {
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.get(face).writeData(data);
            pipePartDirections.get(face).writeData(data);
        }
    }

    public void readData(ByteBuf data) {
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.get(face).readData(data);
            pipePartDirections.get(face).readData(data);
        }
    }
}
