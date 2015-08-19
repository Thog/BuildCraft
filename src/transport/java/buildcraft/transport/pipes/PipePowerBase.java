package buildcraft.transport.pipes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.utils.Average;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

import io.netty.buffer.ByteBuf;

public abstract class PipePowerBase extends Pipe<PipeTransportPower>implements IMjExternalStorage, IDebuggable, ISerializable {
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

    protected EnumMap<EnumFacing, DefaultMjInternalStorage> pipePartMap = Maps.newEnumMap(EnumFacing.class);
    protected EnumMap<EnumFacing, Average> pipePartDirections = Maps.newEnumMap(EnumFacing.class);

    public PipePowerBase(Item item) {
        super(new PipeTransportPower(), item);

        transport.initFromPipe(getClass());
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
        // transferPower();
        // transferPower2nd();
        transferPower3rd();
    }

    protected void transferPower3rd() {
        // First, try to flow power around internally
        List<EnumFacing> directions = Lists.newArrayList();
        // Sort the directions in order of how much power has flown into them recently
        int numNegative = 0;
        int numMiddle = 0;
        double totalPosPower = 0;
        double totalNegativePower = 0;
        for (EnumFacing face : EnumFacing.values()) {
            int pos = 0;
            if (!container.isPipeConnected(face)) {
                continue;
            }
            double powerFlow = pipePartDirections.get(face).getAverage();
            double power = pipePartMap.get(face).currentPower() / 2d;
            if (powerFlow == 0) {
                numMiddle++;
            } else if (powerFlow < 0) {
                numNegative++;
                totalNegativePower += power;
            } else {
                totalPosPower += power;
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
            heldPower += store.extractPower(getWorld(), 0, store.currentPower() / 2d, false);
        }
        if (numNegative > 0) {
            if (totalNegativePower > 1) {
                for (int i = 0; i < numNegative; i++) {
                    EnumFacing face = directions.get(i);
                    double powerFlow = pipePartDirections.get(face).getAverage();
                    double mj = heldPower * powerFlow / totalNegativePower;
                    heldPower -= mj;
                    heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
                }
            } else {

            }
        }
        if (numMiddle > 0 && heldPower > 0) {
            double totalHeldPower = heldPower;
            for (int i = numNegative; i < numMiddle; i++) {
                EnumFacing face = directions.get(i);
                double mj = totalHeldPower / numMiddle;
                heldPower -= mj;
                heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
            }
        }
        if (heldPower > 0) {
            for (int i = 0; i < directions.size(); i++) {
                EnumFacing face = directions.get(i);
                double powerFlow = pipePartDirections.get(face).getAverage();
                double mj = heldPower * powerFlow / totalPosPower;
                heldPower -= mj;
                heldPower += pipePartMap.get(face).insertPower(getWorld(), mj, false);
            }
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
                pipePartDirections.get(face).push(mj - excess);
                storage.insertPower(getWorld(), excess, false);
            } else if (container.hasPipePluggable(face)) {
                // TODO
            }
        }
    }

    protected void transferPower2nd() {
        // First, try and transfer power from the pipe face to the tile it is connected to
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
                suctionDifference = 16;

                double mj = storage.extractPower(getWorld(), 0, storage.currentPower() * suctionDifference / 16, false);
                double excess = external.insertPower(getWorld(), face, this, mj, false);
                storage.insertPower(getWorld(), excess, false);
            } else if (container.hasPipePluggable(face)) {
                // TODO
            }
        }

        // Second transfer half the total power to the center
        double heldPower = 0;
        double totalSuction = 0;

        for (EnumFacing face : EnumFacing.values()) {

            DefaultMjInternalStorage storage = pipePartMap.get(face);
            double mj = (storage.currentPower() / 2);
            heldPower += storage.extractPower(getWorld(), mj, mj, false);
            if (!container.isPipeConnected(face)) {
                continue;
            }
            totalSuction += storage.getSuction();
        }

        double totalPower = heldPower;

        // Then transfer all the center power to the faces that need it
        for (EnumFacing face : EnumFacing.values()) {
            if (!container.isPipeConnected(face)) {
                continue;
            }
            DefaultMjInternalStorage storage = pipePartMap.get(face);
            double mj = totalPower * storage.getSuction() / totalSuction;
            heldPower -= mj;
            heldPower += storage.insertPower(getWorld(), mj, false);
        }

        // if (heldPower > 0) {
        // // If the above failed then just add power to each part
        // for (EnumFacing face : EnumFacing.values()) {
        // if (!container.isPipeConnected(face)) {
        // continue;
        // }
        // DefaultMjInternalStorage storage = pipePartMap.get(face);
        // heldPower = storage.insertPower(getWorld(), heldPower, false);
        // }
        // }

        if (heldPower > 0.00001) {// Normal losses due to double being slightly inaccurate.
            BCLog.logger.warn("Too much power! (heldPower = " + heldPower + ")");
        }
    }

    protected void transferPower() {
        // Map of pipe part -> the face part they are of
        Map<DefaultMjInternalStorage, EnumFacing> partFaceMap = Maps.newHashMap();
        // Map of pipe face -> the storage it is connected to
        Map<EnumFacing, IMjExternalStorage> faceOtherMap = Maps.newHashMap();
        // Map of pipe part -> suction
        Map<DefaultMjInternalStorage, Double> partSuctionMap = Maps.newHashMap();
        // A list of all the internal storages that have not been visited yet
        List<DefaultMjInternalStorage> toDo = Lists.newArrayList();
        for (EnumFacing face : EnumFacing.values()) {
            if (!container.isPipeConnected(face)) {
                // if there is not a pipe part here then we need to check for pluggables that require power
                if (!container.hasPipePluggable(face)) {
                    continue;
                }

                PipePluggable pluggable = container.getPipePluggable(face);
                if (pluggable instanceof IMjHandler) {
                    IMjExternalStorage external = ((IMjHandler) pluggable).getMjStorage();
                    if (isValidDestination(external, face)) {
                        // If its not a valid destination, then don't add it to the totalSuction calculation below.
                        faceOtherMap.put(face, external);
                    }
                }

                continue;
            }
            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storage.tick(getWorld());
            double thisSuction = storage.getSuction();
            partFaceMap.put(storage, face);
            partSuctionMap.put(storage, thisSuction);
            toDo.add(storage);
            TileEntity tile = container.getWorld().getTileEntity(container.getPos().offset(face));
            if (tile == null || !(tile instanceof IMjHandler)) {
                // Most of the time it SHOULD be a valid handler, but just check anyway
                continue;
            }
            IMjExternalStorage external = ((IMjHandler) tile).getMjStorage();
            if (isValidDestination(external, face)) {
                // If its not a valid destination, then don't add it to the totalSuction calculation below.
                faceOtherMap.put(face, external);
            }
        }

        processDestinations(faceOtherMap);

        for (DefaultMjInternalStorage store : toDo) {
            if (!store.hasActivated()) {
                continue;
            }
            // We need to cache the suction value otherwise the pipes start to pretend that they had different amounts
            // than they did
            double suction = partSuctionMap.get(store);
            // double stored = partStoredMap.get(store);
            EnumFacing face = partFaceMap.get(store);

            List<DefaultMjInternalStorage> toVisit = Lists.newArrayList();

            double totalSuction = 0;
            for (DefaultMjInternalStorage def : toDo) {
                if (def == store) {// use this one for the block the pipe is connected to
                    IMjExternalStorage external = faceOtherMap.get(face);
                    if (external == null) {
                        continue;
                    }
                    double otherSuction = external.getSuction(getWorld(), face);
                    // If the other one has more pull than we do or its a machine (Always try to insert into machines)
                    if (otherSuction > suction || external.getDeviceType(face.getOpposite()) == EnumMjDevice.MACHINE) {
                        totalSuction += otherSuction;
                        toVisit.add(def);
                    }
                } else {
                    double otherSuction = partSuctionMap.get(def);
                    if (otherSuction > suction) {
                        totalSuction += def.getSuction();
                        toVisit.add(def);
                    }
                }
            }

            double totalMj = store.extractPower(getWorld(), 0, 27946, false);
            double usedMj = 0;

            if (totalSuction > 0) {
                for (DefaultMjInternalStorage def : toVisit) {
                    if (def == store) {// The outside machine
                        IMjExternalStorage outside = faceOtherMap.get(def);
                        if (outside != null) {
                            double mj = outside.getSuction(getWorld(), face) * totalMj / totalSuction;
                            double excess = outside.insertPower(getWorld(), face, container.getMjStorage(), mj, false);
                            usedMj += mj - excess;
                        }
                    } else {// Another pipe part
                        double mj = partSuctionMap.get(def) * totalMj / totalSuction;
                        double excess = def.insertPower(getWorld(), mj, false);
                        usedMj += mj - excess;
                    }
                }
            }
            // Re-insert the excess power
            store.insertPower(getWorld(), totalMj - usedMj, false);
        }
    }

    protected boolean isValidDestination(IMjExternalStorage storage, EnumFacing flowDir) {
        return storage.getDeviceType(flowDir.getOpposite()).acceptsPowerFrom(getDeviceType(null));
    }

    protected void processDestinations(Map<EnumFacing, IMjExternalStorage> faceOtherMap) {
        // This is overridden by PipePowerClay to remove any pipes if any machines are present in this map that can
        // accept power (and a few other things)
        for (Entry<EnumFacing, IMjExternalStorage> store : faceOtherMap.entrySet())
            BCLog.logger.info("  " + store.getKey() + " = " + store.getValue());
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

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing face) {
        left.add("");
        left.add("Internal Storage:");
        for (EnumFacing side : EnumFacing.values()) {
            DefaultMjInternalStorage store = pipePartMap.get(side);
            if (store == null) {
                left.add("  - " + side + " = not connected");
            } else {
                left.add("  - " + side + " = " + store.currentPower() + " Mj");
            }
        }
    }

    public void writeData(ByteBuf data) {
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.get(face).writeData(data);
        }
    }

    public void readData(ByteBuf data) {
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.get(face).readData(data);
        }
    }
}
