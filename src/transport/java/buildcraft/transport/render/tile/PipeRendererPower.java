package buildcraft.transport.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import buildcraft.core.lib.EntityResizableCuboid;
import buildcraft.core.lib.render.RenderResizableCuboid;
import buildcraft.core.lib.render.RenderUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipePowerBase;

public class PipeRendererPower {
    public static final float DISPLAY_MULTIPLIER = 0.1f;
    public static final int POWER_STAGES = 32;

    private static int[][][] power = new int[POWER_STAGES][POWER_STAGES][6];
    private static int[] centerPower = new int[POWER_STAGES];

    private static boolean initialized = false;

    static void renderPowerPipe(PipePowerBase pipe, double x, double y, double z) {
        initializeDisplayPowerList();

        PipeTransportPower pow = pipe.transport;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        // GL11.glEnable(GL11.GL_BLEND);

        GL11.glTranslatef((float) x, (float) y, (float) z);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        // Used for the centre rendering
        double maxPower = 0;
        for (EnumFacing face : EnumFacing.values()) {
            double power = pipe.currentPower(face) / pipe.maxPower(face);
            power = Math.sqrt(power);// This will actually increase the value
            if (maxPower < power) {
                maxPower = power;
            }
        }
        short centerStage = (short) ((POWER_STAGES - 1) * maxPower);
        if (centerStage == 0 && maxPower != 0) {
            centerStage = 1;
        }
        if (centerStage >= POWER_STAGES) {
            centerStage = POWER_STAGES - 1;
        }

        GL11.glPushMatrix();
        GL11.glCallList(centerPower[centerStage]);
        GL11.glPopMatrix();

        for (int side = 0; side < 6; ++side) {
            GL11.glPushMatrix();

            short stage = pow.displayPower[side];
            double value = pipe.currentPower(EnumFacing.VALUES[side]) / pipe.maxPower(EnumFacing.VALUES[side]);
            value = Math.sqrt(value);// Make the amount of MJ directly proportional to the AREA not the RADIUS
            stage = (short) ((power.length - 1) * value);
            if (value != 0 && stage == 0) {
                stage = 1;
            }
            if (stage >= 1) {
                if (stage < power.length) {
                    GL11.glCallList(power[stage][centerStage][side]);
                } else {
                    GL11.glCallList(power[power.length - 1][centerStage][side]);
                }
            }

            GL11.glPopMatrix();
        }

        // This was used for something, but no-one knows anymore...
        // TODO (PASS 879): Look this up on github's history
        // bindTexture(STRIPES_TEXTURE);
        // for (int side = 0; side < 6; side += 2) {
        // if (pipe.container.isPipeConnected(EnumFacing.values()[side])) {
        // GL11.glPushMatrix();
        // GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        // GL11.glRotatef(angleY[side], 0, 1, 0);
        // GL11.glRotatef(angleZ[side], 0, 0, 1);
        // float scale = 1.0F - side * 0.0001F;
        // GL11.glScalef(scale, scale, scale);
        // float movement = (0.50F) * pipe.transport.getPistonStage(side / 2);
        // GL11.glTranslatef(-0.25F - 1F / 16F - movement, -0.5F, -0.5F); //
        // float factor = (float) (1.0 / 256.0);
        // float factor = (float) (1.0 / 16.0);
        // box.render(factor);
        // GL11.glPopMatrix();
        // }
        // }

        GlStateManager.enableLighting();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void initializeDisplayPowerList() {
        if (initialized) {
            return;
        }

        initialized = true;

        TextureAtlasSprite normal = BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.Power_Normal.ordinal());

        for (int stage = 0; stage < POWER_STAGES; stage++) {
            int address = GLAllocation.generateDisplayLists(1);
            centerPower[stage] = address;

            GL11.glNewList(address, GL11.GL_COMPILE);

            double width = 0.5 * stage / (double) POWER_STAGES;

            Vec3 size = new Vec3(width, width, width);
            Vec3 pos = new Vec3(0.5, 0.5, 0.5);

            EntityResizableCuboid erc = new EntityResizableCuboid(null);
            erc.setSize(size);
            erc.texture = normal;

            GL11.glPushMatrix();
            RenderUtils.translate(pos);
            RenderResizableCuboid.INSTANCE.renderCubeFromCentre(erc);
            GL11.glPopMatrix();

            GL11.glEndList();
        }
        for (int stage = 0; stage < POWER_STAGES; stage++) {
            for (int centerStage = stage; centerStage < POWER_STAGES; centerStage++) {
                for (int side = 0; side < 6; side++) {
                    int address = GLAllocation.generateDisplayLists(1);
                    power[stage][centerStage][side] = address;

                    GL11.glNewList(address, GL11.GL_COMPILE);

                    double width = 0.5 * stage / (double) POWER_STAGES;
                    double centerOffset = 0.25 * centerStage / (double) POWER_STAGES;

                    EnumFacing face = EnumFacing.values()[side];

                    Vec3 pos = new Vec3(0.5, 0.5, 0.5).add(Utils.convert(face, 0.25 + centerOffset / 2d));

                    face = Utils.convertPositive(face);
                    Vec3 size = new Vec3(1, 1, 1).subtract(Utils.convert(face));
                    size = Utils.multiply(size, width);
                    size = size.add(Utils.convert(face, 0.5 - centerOffset));

                    EntityResizableCuboid erc = new EntityResizableCuboid(null);
                    erc.setSize(size);
                    erc.texture = normal;

                    GL11.glPushMatrix();
                    RenderUtils.translate(pos);
                    RenderResizableCuboid.INSTANCE.renderCubeFromCentre(erc);
                    GL11.glPopMatrix();

                    GL11.glEndList();
                }
            }
        }

    }

    /** Called whenever a texture remap is done, to refresh the existing textures to new ones. */
    // TODO (PASS 1): Call this from a post texture remap event!
    public static void resetTextures() {
        if (!initialized) {
            return;
        }
        initialized = false;

        for (int[][] arr2 : power) {
            for (int[] arr : arr2) {
                for (int i : arr) {
                    GLAllocation.deleteDisplayLists(i);
                }
            }
        }

        for (int i : centerPower) {
            GLAllocation.deleteDisplayLists(i);
        }
    }
}
