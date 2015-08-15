package buildcraft.api.mj;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

/** This particular reference implemtation is not mean to be used, rather is a base example that you can follow to
 * properly implement a machine that uses power */
public final class ReferenceMjMachine extends TileEntity implements IMjHandler, IUpdatePlayerListBox {
    private final IMjExternalStorage externalStorage;
    private final IMjInternalStorage internalStorage;

    public ReferenceMjMachine() {
        // Create our storage things
        externalStorage = new DefaultMjExternalStorage(EnumMjDeviceType.MACHINE, EnumMjPowerType.NORMAL, 40);
        internalStorage = new DefaultMjInternalStorage(400, 40, 600, 0.2);
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
            // Take some power (between 2 and 4, ideally the highest available power though)
            double mj = internalStorage.extractPower(getWorld(), 2, 4, false);
            doAThing(mj);
        }
    }

    /** A function that would use the power for something */
    private void doAThing(double mj) {
        // Super special thing to do goes here
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
