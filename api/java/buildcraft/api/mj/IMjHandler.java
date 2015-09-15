package buildcraft.api.mj;

/** Any object (TileEntity, Entity or anything that has a single instance with data- so not items or blocks) that wishes
 * to deal with power (in any way) should implement this, and pass a version of IMjExternalStorage to actually deal with
 * power. */
public interface IMjHandler {
    /** @return An instance of IMjExternalStorage that actually handles power. Should NEVER return null. */
    IMjExternalStorage getMjStorage();
}
