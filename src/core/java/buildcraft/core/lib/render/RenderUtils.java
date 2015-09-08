/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.render;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public final class RenderUtils {

    /** Deactivate constructor */
    private RenderUtils() {}

    public static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static void translate(Vec3 vector) {
        GL11.glTranslated(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public static void glVertex(Vec3i pos) {
        GL11.glVertex3i(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void glVertex(Vec3 vec) {
        GL11.glVertex3d(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static void glVertex(Vector3f vec) {
        GL11.glVertex3d(vec.x, vec.y, vec.z);
    }
}
