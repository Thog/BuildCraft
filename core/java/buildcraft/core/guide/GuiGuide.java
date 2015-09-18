package buildcraft.core.guide;

import java.io.IOException;

import com.google.common.base.Throwables;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import buildcraft.core.gui.GuiTexture.GuiIcon;
import buildcraft.core.gui.GuiTexture.Rectangle;

public class GuiGuide extends GuiScreen {
    private static final ResourceLocation ICONS = new ResourceLocation("buildcraftcore:textures/gui/guide/icons.png");
    private static final ResourceLocation COVER = new ResourceLocation("buildcraftcore:textures/gui/guide/cover.png");
    private static final ResourceLocation LEFT_PAGE = new ResourceLocation("buildcraftcore:textures/gui/guide/left_page.png");
    private static final ResourceLocation RIGHT_PAGE = new ResourceLocation("buildcraftcore:textures/gui/guide/right_page.png");

    public static final GuiIcon BOOK_COVER = new GuiIcon(COVER, 0, 0, 202, 248);
    public static final GuiIcon BOOK_BINDING = new GuiIcon(COVER, 204, 0, 11, 248);

    public static final GuiIcon PAGE_LEFT = new GuiIcon(LEFT_PAGE, 0, 0, 193, 248);
    public static final GuiIcon PAGE_RIGHT = new GuiIcon(RIGHT_PAGE, 0, 0, 193, 248);

    public static final Rectangle PAGE_LEFT_TEXT = new Rectangle(0, 0, 0, 0);

    public static final GuiIcon PEN_UP = new GuiIcon(ICONS, 0, 0, 17, 135);
    public static final GuiIcon PEN_ANGLED = new GuiIcon(ICONS, 0, 17, 100, 100);

    public static final GuiIcon PEN_HIDDEN_MIN = new GuiIcon(ICONS, 0, 4, 10, 5);
    public static final GuiIcon PEN_HIDDEN_MAX = new GuiIcon(ICONS, 0, 4, 10, 15);

    public static final GuiIcon BOX_EMPTY = new GuiIcon(ICONS, 0, 164, 16, 16);
    public static final GuiIcon BOX_MINUS = new GuiIcon(ICONS, 16, 164, 16, 16);
    public static final GuiIcon BOX_PLUS = new GuiIcon(ICONS, 32, 164, 16, 16);
    public static final GuiIcon BOX_TICKED = new GuiIcon(ICONS, 48, 164, 16, 16);

    public static final GuiIcon BOX_SELECTED_EMPTY = new GuiIcon(ICONS, 0, 180, 16, 16);
    public static final GuiIcon BOX_SELECTED_MINUS = new GuiIcon(ICONS, 16, 180, 16, 16);
    public static final GuiIcon BOX_SELECTED_PLUS = new GuiIcon(ICONS, 32, 180, 16, 16);
    public static final GuiIcon BOX_SELECTED_TICKED = new GuiIcon(ICONS, 48, 180, 16, 16);

    // REMOVE FROM HERE...
    private static final int BOOK_COVER_X = 0, BOOK_COVER_Y = 0, BOOK_COVER_WIDTH = 202, BOOK_COVER_HEIGHT = 248;
    private static final int BOOK_BINDING_X = 204, BOOK_BINDING_Y = 0, BOOK_BINDING_WIDTH = 11, BOOK_BINDING_HEIGHT = 248;
    // TODO: Book cover texture
    private static final int BOOK_DOUBLE_WIDTH = 386, BOOK_DOUBLE_HEIGHT = 248;

    private static final int PAGE_LEFT_X = 0, PAGE_LEFT_Y = 0, PAGE_LEFT_WIDTH = 193, PAGE_LEFT_HEIGHT = 248;
    private static final int PAGE_RIGHT_X = 0, PAGE_RIGHT_Y = 0, PAGE_RIGHT_WIDTH = 193, PAGE_RIGHT_HEIGHT = 248;

    /** Where */
    private static final int PAGE_LEFT_TEXT_X = 31, PAGE_LEFT_TEXT_Y = 22, PAGE_LEFT_TEXT_WIDTH = 141, PAGE_LEFT_TEXT_HEIGHT = 193;
    private static final int PAGE_RIGHT_TEXT_X = 20, PAGE_RIGHT_TEXT_Y = 22, PAGE_RIGHT_TEXT_WIDTH = 141, PAGE_RIGHT_TEXT_HEIGHT = 193;

    private static final int PEN_UP_Y = 0, PEN_UP_X = 0, PEN_UP_WIDTH = 17, PEN_UP_HEIGHT = 135;
    private static final int PEN_ANGLED_Y = 0, PEN_ANGLED_X = 17, PEN_ANGLED_WIDTH = 100, PEN_ANGLED_HEIGHT = 100;

    private static final int PEN_HIDDEN_Y = 0, PEN_HIDDEN_X = 4, PEN_HIDDEN_WIDTH = 10;
    private static final int PEN_HIDDEN_HEIGHT_MIN = 5, PEN_HIDDEN_HEIGHT_MAX = 15;

    // TO HERE

    private static final int PEN_HIDDEN_BOX_X_MIN = PAGE_LEFT_WIDTH - PEN_HIDDEN_WIDTH / 2;
    private static final int PEN_HIDDEN_BOX_Y_MIN = -PEN_HIDDEN_HEIGHT_MAX;
    private static final int PEN_HIDDEN_BOX_X_MAX = PAGE_LEFT_WIDTH + PEN_HIDDEN_WIDTH / 2;
    private static final int PEN_HIDDEN_BOX_Y_MAX = 0;

    private static final float PEN_HOVER_TIME = 0.5f;
    private static final float BOOK_OPEN_TIME = 2f;

    private boolean isOpen = false, isEditing = false, isClosing = false;
    private boolean isOpening = false;

    /** Float between 0 and height + {@link #BOOK_COVER_HEIGHT} */
    private float movingStage = 0;

    /** Float between -90 and 90} */
    private float openingAngle = -90;

    /** Float between {@link #PEN_HIDDEN_HEIGHT_MIN} and {@link #PEN_HIDDEN_HEIGHT_MAX} */
    private float hoverStage = 0;
    private boolean isOverHover = false;

    /** In seconds since this gui was opened */
    private float time = 0;
    /** How long since the last {@link #drawScreen(int, int, float)} was called in seconds */
    private float diff = 0;
    private int minX, minY;
    /** The current mouse positions. Used by the GuideFontRenderer */
    public int mouseX, mouseY;

    private GuidePage currentPage;

    public GuiGuide() {
        currentPage = new GuideMenu();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        time += partialTicks / 20f;
        diff = partialTicks / 20f;
        minX = (width - BOOK_DOUBLE_WIDTH) / 2;
        minY = (height - BOOK_DOUBLE_HEIGHT) / 2;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        try {
            if (isOpen) {
                drawOpen();
            } else if (isOpening) {
                drawOpening();
            } else {
                drawCover();
            }
        } catch (Throwable t) {
            // Temporary fix for crash report classes crashing so we can see the ACTUAL error
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }

    private void drawCover() {
        minX = (width - BOOK_COVER_WIDTH) / 2;
        minY = (height - BOOK_COVER_HEIGHT) / 2;

        mc.renderEngine.bindTexture(COVER);
        BOOK_COVER.draw(minX, minY);
    }

    private void drawOpening() {
        minX = (width - BOOK_COVER_WIDTH) / 2;
        minY = (height - BOOK_COVER_HEIGHT) / 2;

        openingAngle += (diff / BOOK_OPEN_TIME) * 180;
        float sin = MathHelper.sin((float) (openingAngle * Math.PI / 180));
        if (sin < 0) {
            sin *= -1;
        }
        if (openingAngle >= 90) {
            isOpen = true;
        }
        if (openingAngle < 0) {
            minX = (width - BOOK_COVER_WIDTH) / 2;
            minY = (height - BOOK_COVER_HEIGHT) / 2;

            int coverWidth = (int) (sin * BOOK_COVER_WIDTH);
            sin = 1 - sin;
            int bindingWidth = (int) (sin * BOOK_BINDING_WIDTH);

            mc.renderEngine.bindTexture(RIGHT_PAGE);
            PAGE_RIGHT.draw(minX + BOOK_COVER_WIDTH - PAGE_RIGHT_WIDTH, minY);

            mc.renderEngine.bindTexture(COVER);
            BOOK_COVER.drawScaled(minX, minY, coverWidth, BOOK_COVER.height);

            BOOK_BINDING.drawScaled(minX + coverWidth, minY, bindingWidth, BOOK_BINDING.height);

        } else if (openingAngle == 0) {
            minX = (width - BOOK_COVER_WIDTH) / 2;
            minY = (height - BOOK_COVER_HEIGHT) / 2;

            mc.renderEngine.bindTexture(RIGHT_PAGE);
            PAGE_RIGHT.draw(minX + BOOK_COVER.width - PAGE_LEFT.width, minY);

            mc.renderEngine.bindTexture(COVER);
            BOOK_COVER.draw(minX, minY);
        } else if (openingAngle > 0) {
            int pageWidth = (int) (sin * PAGE_LEFT.width);
            int bindingWidth = (int) ((1 - sin) * BOOK_BINDING.width);

            int penHeight = (int) (sin * PEN_HIDDEN_HEIGHT_MIN);

            minX = (width - BOOK_COVER.width - pageWidth) / 2;
            minY = (height - BOOK_COVER.height) / 2;

            mc.renderEngine.bindTexture(RIGHT_PAGE);
            PAGE_RIGHT.draw(minX + pageWidth + bindingWidth, minY);

            mc.renderEngine.bindTexture(LEFT_PAGE);
            PAGE_LEFT.drawScaled(minX + bindingWidth, minY, pageWidth, PAGE_LEFT.height);

            mc.renderEngine.bindTexture(COVER);
            BOOK_BINDING.drawScaled(minX, minY, bindingWidth, BOOK_BINDING.height);

            mc.renderEngine.bindTexture(ICONS);
            drawTexturedModalRect(minX + pageWidth + bindingWidth - (PEN_HIDDEN_WIDTH / 2), minY - penHeight, PEN_HIDDEN_X, PEN_HIDDEN_Y,
                    PEN_HIDDEN_WIDTH, penHeight);
        }
    }

    private void drawOpen() {
        // Draw the pages
        mc.renderEngine.bindTexture(LEFT_PAGE);
        PAGE_LEFT.draw(minX, minY);

        mc.renderEngine.bindTexture(RIGHT_PAGE);
        PAGE_RIGHT.draw(minX + PAGE_LEFT.width, minY);

        isOverHover = mouseX >= minX + PEN_HIDDEN_BOX_X_MIN && mouseX <= minX + PEN_HIDDEN_BOX_X_MAX && mouseY >= minY + PEN_HIDDEN_BOX_Y_MIN
            && mouseY <= minY + PEN_HIDDEN_BOX_Y_MAX;

        // Now draw the actual contents of the book
        currentPage.fontRenderer = fontRendererObj;
        currentPage.tick(diff);
        currentPage.renderFirstPage(minX + PAGE_LEFT_TEXT_X, minY + PAGE_LEFT_TEXT_Y, PAGE_LEFT_TEXT_WIDTH, PAGE_LEFT_TEXT_HEIGHT);
        currentPage.renderSecondPage(minX + PAGE_LEFT_WIDTH + PAGE_RIGHT_TEXT_X, minY + PAGE_RIGHT_TEXT_Y, PAGE_RIGHT_TEXT_WIDTH,
                PAGE_RIGHT_TEXT_HEIGHT);

        GlStateManager.color(1, 1, 1);

        // Draw the pen
        if (isEditing) {
            mc.renderEngine.bindTexture(ICONS);

            if (isOverHover) {
                drawTexturedModalRect(mouseX - PEN_UP_WIDTH / 2, mouseY - PEN_UP_HEIGHT - 2, PEN_UP_X, PEN_UP_Y, PEN_UP_WIDTH, PEN_UP_HEIGHT);
            } else {
                drawTexturedModalRect(mouseX - 2, mouseY - PEN_ANGLED_HEIGHT - 2, PEN_ANGLED_X, PEN_ANGLED_Y, PEN_ANGLED_WIDTH, PEN_ANGLED_HEIGHT);
            }
        } else {
            // Calculate pen hover position
            float hoverDiff = (diff / PEN_HOVER_TIME) * (PEN_HIDDEN_HEIGHT_MAX - PEN_HIDDEN_HEIGHT_MIN);
            if (hoverStage > PEN_HIDDEN_HEIGHT_MAX) {
                hoverStage -= hoverDiff * 5;
            } else if (isOverHover) {
                hoverStage += hoverDiff;
                if (hoverStage > PEN_HIDDEN_HEIGHT_MAX) {
                    hoverStage = PEN_HIDDEN_HEIGHT_MAX;
                }
            } else {
                if (hoverStage > PEN_HIDDEN_HEIGHT_MIN) {
                    hoverStage -= hoverDiff;
                }
                if (hoverStage < PEN_HIDDEN_HEIGHT_MIN) {
                    hoverStage = PEN_HIDDEN_HEIGHT_MIN;
                }
            }
            int height = (int) hoverStage;

            // Draw pen
            mc.renderEngine.bindTexture(ICONS);
            drawTexturedModalRect(minX + PAGE_LEFT_WIDTH - PEN_HIDDEN_WIDTH / 2, minY - height, PEN_HIDDEN_X, PEN_HIDDEN_Y, PEN_HIDDEN_WIDTH, height);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            if (isOpen) {
                if (isEditing) {

                }

                if (isOverHover) {
                    isEditing = !isEditing;
                    if (!isEditing) {
                        hoverStage = PEN_UP_HEIGHT;
                    }
                }
            } else {
                if (mouseX >= minX && mouseY >= minY && mouseX <= minX + BOOK_COVER_WIDTH && mouseY <= minY + BOOK_COVER_HEIGHT) {
                    if (isOpening) {// So people can double-click to open it instantly
                        isOpen = true;
                    }
                    isOpening = true;
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
