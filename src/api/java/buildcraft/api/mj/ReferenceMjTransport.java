package buildcraft.api.mj;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** This particular reference implemtation is not mean to be used, rather is an example that you can follow to properly
 * implement a tile entity that transports power. */
public final class ReferenceMjTransport extends TileEntity implements IMjHandler, IUpdatePlayerListBox {
    private final DefaultMjExternalStorage externalStorage;
    private final DefaultMjInternalStorage internalStorage;

    public ReferenceMjTransport() {
        // Create our storage things
        externalStorage = new DefaultMjExternalStorage(EnumMjDeviceType.TRANSPORT, EnumMjPowerType.NORMAL, 20);
        // Max power stored = 40MJ
        // Minimum power required to activate = 20MJ
        // How long to wait before losing power = 20 ticks
        // How much power to lose when ^ is true = 1 MJ / tick
        internalStorage = new DefaultMjInternalStorage(40, 2, 20, 1);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }

    @Override
    public void update() {
        // Process power loss over time
        internalStorage.tick(getWorld());
        // Check if we should activate
        if (internalStorage.hasActivated()) {
            // Take some power (between 1 and 20, ideally the highest available power though)
            // This transport can push up to 20mj/t to all surrounding machines
            double mj = internalStorage.extractPower(getWorld(), 1, 20, false);
            double excess = transferPower(mj);
            // Put the excess power back into the internal storage
            internalStorage.insertPower(getWorld(), excess, false);
        }
    }

    private double transferPower(double mj) {
        double thisSuction = externalStorage.getSuction(getWorld(), null);
        double totalSuction = 0;
        // Setup cache maps
        EnumMap<EnumFacing, Double> suctionMap = Maps.newEnumMap(EnumFacing.class);
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
            double otherSuction = storage.getSuction(getWorld(), face.getOpposite());
            // Only flow into things that require power more than we do, or if they are a machine (as they always
            // require power more than transports do)
            if (otherSuction > thisSuction || storage.getDeviceType() == EnumMjDeviceType.MACHINE) {
                suctionMap.put(face, otherSuction);
                storageMap.put(face, storage);
                totalSuction += otherSuction;
            }
        }

        if (suctionMap.size() == 0) {// No devices requesting power, don't bother trying
            return mj;
        }

        double overflow = 0;

        for (Entry<EnumFacing, Double> entry : suctionMap.entrySet()) {
            EnumFacing face = entry.getKey();
            double suction = entry.getValue();
            // Only transfer amounts related to how much they actually need it.
            double balance = suction / totalSuction;
            double toGive = mj * balance;
            IMjExternalStorage storage = storageMap.get(face);
            overflow += storage.insertPower(getWorld(), face, getMjStorage(), toGive, false);
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
