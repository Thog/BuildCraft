package buildcraft.api.mj;

/** Any TileEntity that wishes to deal with power (in any way) should implement this, and pass a version of IMjStorage
 * to actually deal with power. */
public interface IMjHandler {
    IMjExternalStorage getMjStorage();
}
