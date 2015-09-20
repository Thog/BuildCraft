package buildcraft.core.guide;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import buildcraft.core.gui.GuiTexture.GuiIcon;
import buildcraft.core.gui.GuiTexture.Rectangle;

public class GuideCrafting extends GuidePart {
    public static final GuiIcon CRAFTING_GRID = new GuiIcon(GuiGuide.ICONS, 119, 0, 116, 54);
    public static final Rectangle[][] ITEM_POSITION = new Rectangle[3][3];
    public static final Rectangle OUT_POSITION = new Rectangle(95, 19, 16, 16);
    public static final Rectangle OFFSET = new Rectangle((GuiGuide.PAGE_LEFT_TEXT.width - CRAFTING_GRID.width) / 2, 0, CRAFTING_GRID.width,
            CRAFTING_GRID.height);

    static {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ITEM_POSITION[x][y] = new Rectangle(1 + x * 18, 1 + y * 18, 16, 16);
            }
        }
    }

    private final ItemStack[][] input;
    private final ItemStack output;

    GuideCrafting(GuiGuide gui, ItemStack[][] input, ItemStack output) {
        super(gui);
        this.input = input;
        this.output = output;
    }

    @Override
    public PagePart renderIntoArea(int x, int y, int width, int height, PagePart current, int index) {
        if (current.line + 4 >= height / LINE_HEIGHT) {
            current = current.newPage();
        }
        x += OFFSET.x;
        y += OFFSET.y + current.line * LINE_HEIGHT;
        if (current.page == index) {
            CRAFTING_GRID.draw(x, y);
            // Render the item
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            for (int itemX = 0; itemX < input.length; itemX++) {
                for (int itemY = 0; itemY < input[itemX].length; itemY++) {
                    Rectangle rect = ITEM_POSITION[itemX][itemY];
                    ItemStack stack = input[itemX][itemY];
                    if (stack != null) {
                        GlStateManager.color(1, 1, 1);
                        gui.mc.getRenderItem().renderItemIntoGUI(stack, x + rect.x, y + rect.y);
                    }
                }
            }
            gui.mc.getRenderItem().renderItemIntoGUI(output, x + OUT_POSITION.x, y + OUT_POSITION.y);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();

            // Render the tooltip
            for (int itemX = 0; itemX < input.length; itemX++) {
                for (int itemY = 0; itemY < input[itemX].length; itemY++) {
                    Rectangle rect = ITEM_POSITION[itemX][itemY];
                    ItemStack stack = input[itemX][itemY];
                    if (stack != null && rect.isMouseInside(x + rect.x, y + rect.y, mouseX, mouseY)) {
                        gui.drawTooltip(stack, mouseX, mouseY);
                    }
                }
            }
            if (OUT_POSITION.isMouseInside(x + OUT_POSITION.x, y + OUT_POSITION.y, mouseX, mouseY)) {
                gui.drawTooltip(output, mouseX, mouseY);
            }
        }
        current = current.nextLine(4, height / LINE_HEIGHT);
        return current;
    }
}
