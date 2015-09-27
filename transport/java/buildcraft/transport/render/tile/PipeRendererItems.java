package buildcraft.transport.render.tile;

import java.util.Map;

import com.google.common.collect.Maps;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import buildcraft.api.items.IItemCustomPipeRender;
import buildcraft.api.transport.IPipe;
import buildcraft.core.lib.EntityResizableCuboid;
import buildcraft.core.lib.render.RenderResizableCuboid;
import buildcraft.core.lib.render.RenderUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.internal.pipes.PipeTransportItems;

public class PipeRendererItems {
    private static final int MAX_ITEMS_TO_RENDER = 10;
    private static final Map<EnumDyeColor, Integer> LIGHT_HEX = Maps.newEnumMap(EnumDyeColor.class);

    private static final EntityItem dummyEntityItem = new EntityItem(null);
    private static final RenderEntityItem customRenderItem;

    static {
        customRenderItem = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
            @Override
            public boolean shouldBob() {
                return false;
            }

            @Override
            public boolean shouldSpreadItems() {
                return false;
            }
        };
        LIGHT_HEX.put(EnumDyeColor.BLACK, 0x181414);
        LIGHT_HEX.put(EnumDyeColor.RED, 0xBE2B27);
        LIGHT_HEX.put(EnumDyeColor.GREEN, 0x007F0E);
        LIGHT_HEX.put(EnumDyeColor.BROWN, 0x89502D);
        LIGHT_HEX.put(EnumDyeColor.BLUE, 0x253193);
        LIGHT_HEX.put(EnumDyeColor.PURPLE, 0x7e34bf);
        LIGHT_HEX.put(EnumDyeColor.CYAN, 0x299799);
        LIGHT_HEX.put(EnumDyeColor.SILVER, 0xa0a7a7);
        LIGHT_HEX.put(EnumDyeColor.GRAY, 0x7A7A7A);
        LIGHT_HEX.put(EnumDyeColor.PINK, 0xD97199);
        LIGHT_HEX.put(EnumDyeColor.LIME, 0x39D52E);
        LIGHT_HEX.put(EnumDyeColor.YELLOW, 0xFFD91C);
        LIGHT_HEX.put(EnumDyeColor.LIGHT_BLUE, 0x66AAFF);
        LIGHT_HEX.put(EnumDyeColor.MAGENTA, 0xD943C6);
        LIGHT_HEX.put(EnumDyeColor.ORANGE, 0xEA7835);
        LIGHT_HEX.put(EnumDyeColor.WHITE, 0xe4e4e4);
    }

    public static void renderItemPipe(IPipe pipe, PipeTransportItems transport, float f) {
        GL11.glPushMatrix();

        float light = pipe.getTile().getWorld().getLightBrightness(pipe.getTile().getPos());

        int count = 0;
        for (TravelingItem item : transport.items) {
            if (count >= MAX_ITEMS_TO_RENDER) {
                break;
            }

            if (item == null || item.pos == null) {
                continue;
            }

            EnumFacing face = item.toCenter ? item.input : item.output;
            Vec3 motion = Utils.convert(face, item.getSpeed() * f);

            Vec3 pos = item.pos.subtract(Utils.convert(pipe.getTile().getPos())).add(motion);
            doRenderItem(item, pos, light, item.color);
            count++;
        }

        GL11.glPopMatrix();
    }

    public static void doRenderItem(TravelingItem travellingItem, Vec3 itemPos, float light, EnumDyeColor color) {

        if (travellingItem == null || travellingItem.getItemStack() == null) {
            return;
        }

        float renderScale = 0.7f;
        ItemStack itemstack = travellingItem.getItemStack();

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0.05f, 0);
        RenderUtils.translate(itemPos);
        GL11.glPushMatrix();

        if (travellingItem.hasDisplayList) {
            GL11.glCallList(travellingItem.displayList);
        } else {
            travellingItem.displayList = GLAllocation.generateDisplayLists(1);
            travellingItem.hasDisplayList = true;

            GL11.glNewList(travellingItem.displayList, GL11.GL_COMPILE_AND_EXECUTE);
            if (itemstack.getItem() instanceof IItemCustomPipeRender) {
                IItemCustomPipeRender render = (IItemCustomPipeRender) itemstack.getItem();
                float itemScale = render.getPipeRenderScale(itemstack);
                GL11.glScalef(renderScale * itemScale, renderScale * itemScale, renderScale * itemScale);
                itemScale = 1 / itemScale;

                if (!render.renderItemInPipe(itemstack)) {
                    dummyEntityItem.setEntityItemStack(itemstack);
                    customRenderItem.doRender(dummyEntityItem, 0, 0, 0, 0, 0);
                }

                GL11.glScalef(itemScale, itemScale, itemScale);
            } else {
                GL11.glScalef(renderScale, renderScale, renderScale);
                dummyEntityItem.setEntityItemStack(itemstack);
                customRenderItem.doRender(dummyEntityItem, 0, 0, 0, 0, 0);
            }
            GL11.glEndList();
        }
        GL11.glPopMatrix();
        if (color != null) {// The box around an item that decides what colour lenses it can go through
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

            EntityResizableCuboid erc = new EntityResizableCuboid(null);
            erc.texture = null;// BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.ItemBox.ordinal());
            erc.xSize = 1;
            erc.ySize = 1;
            erc.zSize = 1;

            GL11.glPushMatrix();
            renderScale /= 2f;
            GL11.glTranslatef(0, 0.2f, 0);
            GL11.glScalef(renderScale, renderScale, renderScale);
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

            RenderUtils.setGLColorFromInt(LIGHT_HEX.get(color));
            RenderResizableCuboid.INSTANCE.renderCube(erc);
            GlStateManager.color(1, 1, 1, 1);

            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
}
