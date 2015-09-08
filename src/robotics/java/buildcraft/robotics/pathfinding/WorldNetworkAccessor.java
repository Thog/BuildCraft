package buildcraft.robotics.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IWorldAccess;

/** Used to listen to the world for updates and automatically make the WorldNetworkManager add or remove volumes and
 * paths as necessary. This is mostly just a class of empty methods to separate the implementation of the IWorldAccess
 * interface from WorldNetworkManager */
class WorldNetworkAccessor implements IWorldAccess {
    final ServerNetworkManager manager;

    WorldNetworkAccessor(ServerNetworkManager manager) {
        this.manager = manager;
    }

    @Override
    public void markBlockForUpdate(BlockPos pos) {
        manager.blockChanged(pos);
    }

    @Override
    public void notifyLightSet(BlockPos pos) {}

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}

    @Override
    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {}

    @Override
    public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {}

    @Override
    public void spawnParticle(int id, boolean bool, double x, double y, double z, double xOffset, double yOffset, double zOffset, int... thingy) {}

    @Override
    public void onEntityAdded(Entity entityIn) {}

    @Override
    public void onEntityRemoved(Entity entityIn) {}

    @Override
    public void playRecord(String recordName, BlockPos blockPosIn) {}

    @Override
    public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {}

    @Override
    public void playAusSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_) {}

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}
