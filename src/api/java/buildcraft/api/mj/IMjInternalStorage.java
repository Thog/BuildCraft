package buildcraft.api.mj;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Use this internally to your tile entity to take power from IMjStorage without calling the various methods which
 * change the state of the tile entity. This so done to bypass the normal checks on what can take power from or give
 * power to this device. */
public interface IMjInternalStorage {
    /** @return The current power level inside this storage. */
    double currentPower();

    /** @return The maximum power this storage can hold. */
    double maxPower();

    /** @return A double between 0 and 1 that represents how much this storage is pulling power in. */
    double getSuction();

    /** If this has enough power then this will activate the machine, so that the machine can continue running until it
     * has no power left
     * 
     * @param min The minimum amount of power to take. If this much power cannot be taken, then it will return 0 power.
     * @param max The maximum amount of power to take.
     * @param simulate If true, then only pretend you took power from this device (essentially this will run without
     *            side effects)
     * @return The amount of power taken from storage. */
    double extractPower(World world, double min, double max, boolean simulate);

    /** @param mj The amount of MJ to give to this device
     * @param simulate If true, then only pretend you gave this device any power (essentially this will run without side
     *            effects)
     * @return The amount of overflow power that this device could not accept */
    double insertPower(World world, double mj, boolean simulate);

    /** This should be called every tick from the tile entity. Generally this should be called before calling any of the
     * modifying operations in the tick method. It is safe to call this method many times during the same tick. */
    void tick(World worldObj);

    /** @return True if this has enough power to start operation or continue operation if the device was operating
     *         previously operating. */
    boolean hasActivated();

    /** @return True if the machine has been activated by extracting power from it. */
    boolean isOperating();

    /** Stop the machine from operating if it does not have enough power for activation (This will only make
     * {@link #hasActivated(World)} return false if it does not have enough energy to continue) */
    void stopOperating();

    NBTTagCompound writeToNBT();

    void readFromNBT(NBTTagCompound nbt);
}
