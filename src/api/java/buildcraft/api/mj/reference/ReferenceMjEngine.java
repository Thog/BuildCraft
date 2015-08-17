package buildcraft.api.mj.reference;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.IMjInternalStorage;

/** This particular engine implementation gives its power upwards, and pulses its power production every 40 ticks. */
public class ReferenceMjEngine extends TileEntity implements IMjHandler, IUpdatePlayerListBox {
    private final IMjExternalStorage externalStorage;
    private final IMjInternalStorage internalStorage;

    public ReferenceMjEngine() {
        // Create our storage things
        externalStorage = new DefaultMjExternalStorage(EnumMjDevice.ENGINE, EnumMjPower.NORMAL, 40);
        internalStorage = new DefaultMjInternalStorage(400, 40, 600, 0.2);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }

    @Override
    public void update() {
        if (hasItemsToBurn()) {
            internalStorage.insertPower(getWorld(), 1, false);
        }
        // Process power loss over time
        internalStorage.tick(getWorld());
        // Check if we should give out power
        if (internalStorage.hasActivated()) {
            // Take some power (between 10 and 20, ideally the highest available power though)
            double mj = internalStorage.extractPower(getWorld(), 10, 20, false);
            // Power the above tile
            double left = powerAbove(mj);
            // Give back how much power was not given to the above one back.
            internalStorage.insertPower(getWorld(), left, false);
        }
    }

    private boolean hasItemsToBurn() {
        // Normally this would test an inventory, consume items etc, but for demonstration purposes this is dependent on
        // the current world time (its on every other 1000 ticks)
        long time = getWorld().getTotalWorldTime();
        time /= 1000;
        time %= 2;
        return time == 0;
    }

    private double powerAbove(double mj) {
        // Get the tile to power (in this case, above)
        TileEntity tileAbove = worldObj.getTileEntity(getPos().up());
        // Test if we can power it
        if (tileAbove != null && tileAbove instanceof IMjHandler) {
            // Get its handler
            IMjHandler handler = (IMjHandler) tileAbove;
            // Get the external storage we can try to give power to
            IMjExternalStorage storage = handler.getMjStorage();
            // Try and give it power
            double leftover = storage.insertPower(getWorld(), EnumFacing.UP, getMjStorage(), mj, false);
            // Return whatever was leftover back
            return leftover;
        }
        return mj;
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
