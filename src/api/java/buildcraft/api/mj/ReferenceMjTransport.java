package buildcraft.api.mj;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** This particular reference implemtation is not mean to be used, rather is a base example that you can follow to
 * properly implement a machine that transports power. */
public final class ReferenceMjTransport extends TileEntity implements IMjHandler, IUpdatePlayerListBox {
    private final IMjExternalStorage externalStorage;
    private final IMjInternalStorage internalStorage;

    public ReferenceMjTransport() {
        // Create our storage things
        externalStorage = new DefaultMjExternalStorage(EnumMjType.TRANSPORT);
        internalStorage = new DefaultMjInternalStorage(40, 2, 20, 1);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }

    @Override
    public void update() {
        // Check if we should activate
        if (internalStorage.tick(getWorld())) {
            // Take some power (between 1 and 20, ideally the highest available power though)
            // This transport can push up to 20mj/t to all surrounding machines
            double mj = internalStorage.takePower(getWorld(), 1, 20, false);
            double excess = transferPower(mj);
            internalStorage.givePower(getWorld(), excess, false);
        }
    }

    private double transferPower(double mj) {
        double thisFlow = externalStorage.getFlow();
        double totalFlow = 0;
        // Setup cache maps
        EnumMap<EnumFacing, Double> flowMap = Maps.newEnumMap(EnumFacing.class);
        EnumMap<EnumFacing, IMjExternalStorage> storageMap = Maps.newEnumMap(EnumFacing.class);
        // Test all faces
        for (EnumFacing face : EnumFacing.values()) {
            // Make sure this face actually handles power
            TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
            if (tile == null || !(tile instanceof IMjHandler)) {
                continue;
            }
            // Get the external storage
            IMjHandler handler = (IMjHandler) tile;
            IMjExternalStorage storage = handler.getMjStorage();
            double otherFlow = storage.getFlow();
            // Only flow into things that require power more than we do, or if they are a machine (as they always
            // require power more than transports do)
            if (otherFlow < thisFlow || storage.getType() == EnumMjType.MACHINE) {
                flowMap.put(face, otherFlow);
                storageMap.put(face, storage);
                totalFlow += otherFlow;
            }
        }
        double overflow = 0;

        for (Entry<EnumFacing, Double> entry : flowMap.entrySet()) {
            EnumFacing face = entry.getKey();
            double flow = entry.getValue();
            // Only transfer amounts related to how much they actually need it.
            double balance = flow / totalFlow;
            double toGive = mj * balance;
            IMjExternalStorage storage = storageMap.get(face);
            overflow += storage.recievePower(getWorld(), face, getMjStorage(), toGive, false);
        }
        return overflow;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        internalStorage.readFromNBT(compound.getCompoundTag("mj"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("mj", internalStorage.writeToNBT());
    }
}
