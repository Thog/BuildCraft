package buildcraft.robotics.pathfinding;

import java.util.Set;

import javax.vecmath.Vector3f;

import com.google.common.collect.Sets;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.core.lib.render.BuildCraftBakedModel;
import buildcraft.core.lib.render.RenderUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.robotics.BuildCraftRobotics;
import buildcraft.robotics.pathfinding.BlockPath.PathDirection;

public class RobotPathRenderer {
    private Minecraft mc;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderOverlay(RenderWorldLastEvent event) {
        mc = Minecraft.getMinecraft();

        EntityPlayer player = mc.thePlayer;
        ItemStack helmet = player.getCurrentArmor(3);
        if (helmet == null || helmet.getItem() != BuildCraftRobotics.gogglesItem) {
            return;
        }

        Vec3 interpPosition = Utils.getInterpolatedVec(player, event.partialTicks);

        GL11.glPushMatrix();
        RenderUtils.translate(Utils.multiply(interpPosition, -1));

        // BlockPos playerPos = Utils.convertFloor(Utils.getVec(player));

        // int radius = 100;
        Set<BlockVolume> volumes = Sets.newIdentityHashSet();
        Set<BlockPath> paths = Sets.newIdentityHashSet();
        Set<BlockArea> areas = Sets.newIdentityHashSet();

        // Render the network
        WorldNetworkManager network = WorldNetworkManager.getForWorld(player.getEntityWorld());

        volumes = network.volumeSet;
        paths = network.pathSet;
        //
        // for (BlockPos pos : Utils.allInBoxIncludingCorners(playerPos.add(-radius, -radius, -radius),
        // playerPos.add(radius, radius, radius))) {
        // volumes.add(network.getVolume(pos));
        // paths.add(network.getPath(pos));
        // }

        GL11.glLineWidth(2);
        GlStateManager.disableTexture2D();

        for (BlockVolume volume : volumes) {
            areas.addAll(volume.getAreas());
            drawBlockVolume(volume);
        }

        for (BlockArea area : areas) {
            // drawBlockArea(area);
        }

        for (BlockPath path : paths) {
            drawBlockPath(path);
        }

        GL11.glColor3f(1, 1, 1);

        GlStateManager.enableTexture2D();

        GL11.glPopMatrix();
    }

    private void drawBlockVolume(BlockVolume volume) {
        GL11.glColor3f(0, 1, 1);
        drawVolume(volume.min, volume.max, 1.01f);
    }

    private void drawBlockPath(BlockPath path) {
        GL11.glColor3f(0, 1, 0);
        BlockPos current = path.start;
        for (EnumFacing face : path.path.get(PathDirection.START_TO_END)) {
            BlockPos old = current;
            current = current.offset(face);
            Vec3 one = Utils.convertMiddle(old);
            Vec3 two = Utils.convertMiddle(current);
            line(one, two);
            GL11.glColor3f(0, 0, 1);
        }
    }

    private void drawBlockArea(BlockArea area) {
        GL11.glColor3f(0.5f, 0, 0.5f);
        drawVolume(area.min, area.max, 0.99f);
    }

    private void drawVolume(BlockPos one, BlockPos two, float radiusScale) {
        Vec3 min = Utils.convertMiddle(Utils.min(one, two));
        Vec3 max = Utils.convertMiddle(Utils.max(one, two));

        Vector3f radius = Utils.convertFloat(Utils.multiply(max.subtract(min), 0.5));
        Vector3f center = Utils.convertFloat(max);
        center.sub(radius);

        radius.scale(radiusScale);

        for (EnumFacing face : EnumFacing.values()) {
            Vector3f[] points = BuildCraftBakedModel.getPointsForFace(face, center, radius);
            for (int i = 0; i < 4; i++) {
                line(Utils.convert(points[i]), Utils.convert(points[(i + 1) % 4]));
            }
        }
    }

    private void line(Vec3 one, Vec3 two) {
        GL11.glBegin(GL11.GL_LINES);
        RenderUtils.glVertex(one);
        RenderUtils.glVertex(two);
        GL11.glEnd();
    }

    private void drawText(String toDisplay, int colour) {
        GL11.glPushMatrix();
        GL11.glScalef(1 / 16f, -1 / 16f, 1 / 16f);
        mc.fontRendererObj.drawString(toDisplay, 0, 0, colour);
        GL11.glScalef(-1, 1, -1);
        mc.fontRendererObj.drawString(toDisplay, 0, 0, 0xFFFFFF);
        GL11.glPopMatrix();
    }
}
