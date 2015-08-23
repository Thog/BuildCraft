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
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.PipeTransportPower;

public class PipeRendererPower {
    public static final float DISPLAY_MULTIPLIER = 0.1f;
    public static final byte POWER_STAGES = PipeTransportPower.POWER_STAGES;

    private static int[][][] power = new int[POWER_STAGES][POWER_STAGES][6];
    private static int[] centerPower = new int[POWER_STAGES];

    private static boolean initialized = false;

    static void renderPowerPipe(Pipe<PipeTransportPower> pipe, double x, double y, double z) {
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
        byte centerPower = 0;
        EnumFacing centerFlowDir = null;
        byte centerFlow = 0;
        byte[] power = pow.displayPower;
        byte[] flow = pow.displayFlow;
        for (int i = 0; i < 6; i++) {
            byte d = power[i];
            if (d > centerPower) {
                centerPower = d;
                centerFlowDir = EnumFacing.values()[i];
                centerFlow = flow[i];
            }
        }

        long ms = System.currentTimeMillis();

        GL11.glPushMatrix();
        renderCenterPower(centerPower, centerFlowDir, centerFlow);
        GL11.glPopMatrix();

        for (int i = 0; i < 6; i++) {
            EnumFacing face = EnumFacing.values()[i];
            if (!pipe.container.isPipeConnected(face)) {
                continue;
            }
            pow.clientDisplayFlow[i] += flow[i] / 60d;
            if (pow.clientDisplayFlow[i] < 0) {
                pow.clientDisplayFlow[i] = 16;
            }
            if (pow.clientDisplayFlow[i] > 16) {
                pow.clientDisplayFlow[i] = 0;
            }
            renderSidePower(face, power[i], pow.clientDisplayFlow[i], centerPower);
        }

        GlStateManager.enableLighting();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void renderSidePower(EnumFacing face, byte stage, double flow, byte centerStage) {
        if (stage <= 0) {
            return;
        }

        double width = 0.5 * stage / (double) POWER_STAGES;
        double centerRadius = 0.25 * centerStage / (double) POWER_STAGES;
        double textureWidth = width * 16;

        double flowTextureStartOffset = 0;
        double textureStartOffsetWidth = 16 - width;

        Vec3 center = new Vec3(0.5, 0.5, 0.5).add(Utils.convert(face, 0.25 + centerRadius / 2d));

        face = Utils.convertPositive(face);
        Vec3 size = new Vec3(1, 1, 1).subtract(Utils.convert(face));
        size = Utils.multiply(size, width);
        size = size.add(Utils.convert(face, 0.5 - centerRadius));

        EntityResizableCuboid cuboid = new EntityResizableCuboid(null);
        cuboid.setSize(size);
        cuboid.texture = BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.Power_Normal.ordinal());
        cuboid.makeClient();

        double offsetNonFlow = 8 - textureWidth / 2;
        double offsetFlow = flow;

        Vec3 textureOffset = new Vec3(offsetNonFlow, offsetNonFlow, offsetNonFlow);
        textureOffset = textureOffset.add(Utils.convert(face, -offsetNonFlow));
        textureOffset = textureOffset.add(Utils.convert(face, offsetFlow));

        cuboid.textureOffsetX = textureOffset.xCoord;
        cuboid.textureOffsetY = textureOffset.yCoord;
        cuboid.textureOffsetZ = textureOffset.zCoord;

        // offset += 0.0001;
        // if (offset >= 8) {
        // offset = 0;
        // }
        // System.out.println(offset);
        // cuboid.textureOffsetX = 2;//offset;

        GL11.glPushMatrix();
        RenderUtils.translate(center);
        RenderResizableCuboid.INSTANCE.renderCubeFromCentre(cuboid);
        GL11.glPopMatrix();
    }

    private static void renderCenterPower(byte stage, EnumFacing flow, byte flowSpeed) {
        if (stage <= 0) {
            return;
        }
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
