package buildcraft.api.mj;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/** Use this as a wrapper around the actual storage device: it might defer to an instance of IMjInternalStorage for the
 * logic of power, but implement all of the checks necessary to make sure that the caller is allowed to take power (It
 * should check sides, and make sure that the caller is of the correct type- so that an engine is not trying to take
 * power from it. */
public interface IMjExternalStorage {
    /** @return The type of this storage device. Depending on what it is, different actions will be taken when accepting
     *         power and receiving power. Should never return null. */
    EnumMjDeviceType getDeviceType();

    /** @return The type of power this storage deals with. */
    EnumMjPowerType getPowerType();

    /** @param flowDirection The direction that the power should flow (If you are extracting from an engine below, this
     *            would be EnumFacing.UP). If you don't know which direction it is, it is safe to pass null.
     * @param to The IMjStorage that will accept the power. This should never be null!
     * @param minMj The minimum amount of Mj to take. If it is impossible to actually take away that much Mj, it will
     *            return nothing and not affect the internal power.
     * @param maxMj The maximum amount of Mj to take.
     * @param simulate If true, nothing internally will be changed, but it will pretend that power was take from it (You
     *            can use this to test if it has enough power)
     * @return The amount of Mj that was removed from storage. */
    double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate);

    /** @param flowDirection The direction that the power should flow (If you are inserting to an engine below, this
     *            would be EnumFacing.DOWN). If you don't know which direction it is, it is safe to pass null.
     * @param from
     * @param mj
     * @param simulate
     * @return The overflow power that could not be inserted into storage. Will equal <code>mj</code> if this storage is
     *         full, or cannot receive power from that direction. */
    double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate);

    /** @param flowDirection The direction that power would flow INTO this device (If you are querying an engine below,
     *            this would be EnumFacing.DOWN). It is safe to pass null, but some devices may return 0 if they depend
     *            on the direction.
     * 
     * @return The amount of power that is "pulling in" to this device, or that is sucking power into this device. The
     *         default implementation returns values between 0 and 1. This should never return values less than 0.
     *         Higher values mean more suction, lower values mean less suction */
    double getSuction(World world, EnumFacing flowDirection);

    /** This should be called before any other methods are called, as they all usually rely on this object. This is only
     * mean to be called during initialisation of the tile entity. Specifically, this is for an external implementation
     * (Usually DefaultMjExternalStorage) to use a separate class for storing power. (Usually DefaultMjInternalStorage)
     * 
     * @param storage The storage to set. If a storage has already been set (usually by the tile entity that created
     *            this) it will throw an IllegalArgumentException. */
    void setInternalStorage(IMjInternalStorage storage);
}
