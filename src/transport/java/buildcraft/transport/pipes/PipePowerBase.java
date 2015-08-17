package buildcraft.transport.pipes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public abstract class PipePowerBase extends Pipe<PipeTransportPower>implements IMjExternalStorage {
    // TYPES:
    // WOOD --extract from engines
    // COBBLE --doesn't connect to STONE or QUARTZ
    // STONE --doesn't connect to COBBLE or QUARTZ
    // QUARTZ --doesn't connect to COBBLE or STONE
    // SANDSTONE --doesn't connect to machines
    // IRON --power only flows out one way
    // GOLD --not sure
    // CLAY --prioritises machines over pipes
    public static final double MAX_POWER = 128;
    public static final double ACTIVATION = 0.1;
    public static final long LOSS_DELAY = 80;
    public static final double LOSS_RATE = 0.1;

    public static final double TRANSFER_POWER = 40;

    protected EnumMap<EnumFacing, DefaultMjInternalStorage> pipePartMap = Maps.newEnumMap(EnumFacing.class);

    public PipePowerBase(Item item) {
        super(new PipeTransportPower(), item);

        transport.initFromPipe(getClass());
        for (EnumFacing face : EnumFacing.values()) {
            pipePartMap.put(face, new DefaultMjInternalStorage(MAX_POWER, ACTIVATION, LOSS_DELAY, LOSS_RATE));
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
            return mj;// You cannot insert power (directly) to pipes -wooden pipes implement this separately for
                      // redstone engines
        }
        EnumFacing pipePart = flowDirection.getOpposite();
        DefaultMjInternalStorage storage = pipePartMap.get(pipePart);
        return storage.insertPower(world, mj, simulate);
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
        return 1 - (storage.currentPower() / storage.maxPower());
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {
        // NO-OP (Its handled seperately)
    }

    @Override
    public double currentPower(EnumFacing side) {
        return 0;
    }

    @Override
    public double maxPower(EnumFacing side) {
        return 0;
    }

    @Override
    public void update() {
        super.update();
        transferPower();
    }

    protected void transferPower() {
        // Map of pipe part -> the face part they are of
        Map<DefaultMjInternalStorage, EnumFacing> partFaceMap = Maps.newHashMap();
        // Map of pipe face -> the storage it is connected to
        Map<EnumFacing, IMjExternalStorage> faceOtherMap = Maps.newHashMap();
        // Map of pipe part -> suction
        Map<DefaultMjInternalStorage, Double> partSuctionMap = Maps.newHashMap();
        // Map of pipe part -> stored in that section
        Map<DefaultMjInternalStorage, Double> partStoredMap = Maps.newHashMap();
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
                    if (isValidDestination(external)) {
                        // If its not a valid destination, then don't add it to the totalSuction calculation below.
                        faceOtherMap.put(face, external);
                    }
                }

                continue;
            }
            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storage.tick(getWorld());
            if (storage.hasActivated()) {// If it has at least ACTIVATION power
                double thisSuction = 1 - (storage.currentPower() / storage.maxPower());
                partFaceMap.put(storage, face);
                partSuctionMap.put(storage, thisSuction);
                toDo.add(storage);
            }
            TileEntity tile = container.getWorld().getTileEntity(container.getPos().offset(face));
            if (tile == null || !(tile instanceof IMjHandler)) {
                // Most of the time it SHOULD be a valid handler, but just check anyway
                continue;
            }
            IMjExternalStorage external = ((IMjHandler) tile).getMjStorage();
            if (isValidDestination(external)) {
                // If its not a valid destination, then don't add it to the totalSuction calculation below.
                faceOtherMap.put(face, external);
            }
        }

        processDestinations(faceOtherMap);

        for (DefaultMjInternalStorage store : toDo) {
            // We need to cache the suction value otherwise the pipes start to pretend that they had different amounts
            // than they did
            double suction = partSuctionMap.get(store);
            double stored = partStoredMap.get(store);
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
                    if (otherSuction > suction || external.getDeviceType(null) == EnumMjDevice.MACHINE) {
                        totalSuction += otherSuction;
                        toVisit.add(def);
                    }
                } else {
                    double otherSuction = partSuctionMap.get(def);
                    if (otherSuction > suction) {
                        totalSuction += def.currentPower() / def.maxPower();
                        toVisit.add(def);
                    }
                }
            }

            double totalMj = store.extractPower(getWorld(), 0, TRANSFER_POWER, false);
            double totalExcess = 0;

            for (DefaultMjInternalStorage def : toVisit) {
                if (def == store) {// The outside machine
                    double mj = faceOtherMap.get(def).getSuction(getWorld(), face) * totalMj / totalSuction;
                    double excess = faceOtherMap.get(def).insertPower(getWorld(), face, container.getMjStorage(), mj, false);
                    totalExcess += excess;
                } else {// Anotehr pipe
                    double mj = partSuctionMap.get(def) * totalMj / totalSuction;
                    double excess = def.insertPower(getWorld(), mj, false);
                    totalExcess += excess;
                }
            }
            // Re-insert the excess power
            store.insertPower(getWorld(), totalExcess, false);
        }
    }

    protected boolean isValidDestination(IMjExternalStorage storage) {
        return storage.getDeviceType(null).acceptsPowerFrom(getDeviceType(null));
    }

    protected void processDestinations(Map<EnumFacing, IMjExternalStorage> faceOtherMap) {
        // This is overridden by PipePowerClay to remove any pipes if any machines are present in this map that can
        // accept power (and a few otehr things)
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTTagCompound storageTag = data.getCompoundTag("storage");
        for (EnumFacing face : EnumFacing.values()) {
            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storage.readFromNBT(storageTag.getCompoundTag(face.name()));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound storageTag = new NBTTagCompound();
        for (EnumFacing face : EnumFacing.values()) {
            DefaultMjInternalStorage storage = pipePartMap.get(face);
            storageTag.setTag(face.name(), storage.writeToNBT());
        }
        data.setTag("storage", storageTag);
    }
}
