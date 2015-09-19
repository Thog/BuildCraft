package buildcraft.core.guide;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

/** Represents a single page, image or crafting recipe for displaying. Only exists on the client. */
public abstract class GuidePart {
    protected final GuiGuide gui;
    protected FontRenderer fontRenderer;
    protected int mouseX, mouseY;

    GuidePart(GuiGuide gui) {
        this.gui = gui;
    }

    /** Renders a raw line at the position, lowering it appropriately */
    protected void renderTextLine(String text, int x, int y, int colour) {
        fontRenderer.drawString(text, x, y + 8 - (fontRenderer.FONT_HEIGHT / 2), colour);
        GlStateManager.color(1, 1, 1);
    }

    /** @param x
     * @param y
     * @param width
     * @param height
     * @param arguments The current arguments of this part. Can be used for anything.
     * @return The new set of arguments that will be passed to all other calls to this class of that part. */
    public abstract void renderIntoArea(int x, int y, int width, int height);

    public void handleMouseClick(int x, int y, int button, int... arguments) {}

    public void handleMouseDragPartial(int startX, int startY, int currentX, int currentY, int button) {}

    public void handleMouseDragFinish(int startX, int startY, int endX, int endY, int button) {}
}
