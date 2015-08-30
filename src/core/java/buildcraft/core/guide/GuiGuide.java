package buildcraft.core.guide;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiGuide extends GuiScreen {
    private static final ResourceLocation ICONS = new ResourceLocation("buildcraftcore:textures/gui/guide/icons.png");
    private static final ResourceLocation COVER = new ResourceLocation("buildcraftcore:textures/gui/guide/cover.png");
    private static final ResourceLocation LEFT_PAGE = new ResourceLocation("buildcraftcore:textures/gui/guide/left_page.png");
    private static final ResourceLocation RIGHT_PAGE = new ResourceLocation("buildcraftcore:textures/gui/guide/right_page.png");

    private static final int BOOK_COVER_X = 0, BOOK_COVER_Y = 0, BOOK_COVER_WIDTH = 146, BOOK_COVER_HEIGHT = 180;
    private static final int BOOK_BINDING_X = 149, BOOK_BINDING_Y = 0, BOOK_BINDING_WIDTH = 7, BOOK_BINDING_HEIGHT = 180;
    // TODO: Book cover texture
    private static final int BOOK_DOUBLE_WIDTH = 280, BOOK_DOUBLE_HEIGHT = 180;

    private static final int PAGE_LEFT_X = 0, PAGE_LEFT_Y = 0, PAGE_LEFT_WIDTH = 140, PAGE_LEFT_HEIGHT = 180;
    private static final int PAGE_RIGHT_X = 0, PAGE_RIGHT_Y = 0, PAGE_RIGHT_WIDTH = 140, PAGE_RIGHT_HEIGHT = 180;

    private static final int PEN_UP_Y = 0, PEN_UP_X = 0, PEN_UP_WIDTH = 17, PEN_UP_HEIGHT = 135;
    private static final int PEN_ANGLED_Y = 0, PEN_ANGLED_X = 17, PEN_ANGLED_WIDTH = 100, PEN_ANGLED_HEIGHT = 100;

    private static final int PEN_HIDDEN_Y = 0, PEN_HIDDEN_X = 4, PEN_HIDDEN_WIDTH = 10;
    private static final int PEN_HIDDEN_HEIGHT_MIN = 5, PEN_HIDDEN_HEIGHT_MAX = 15;

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
    private int mouseX, mouseY;

    public GuiGuide() {
        System.out.println("Hi!");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        time += partialTicks / 20f;
        diff = partialTicks / 20f;
        minX = (width - BOOK_DOUBLE_WIDTH) / 2;
        minY = (height - BOOK_DOUBLE_HEIGHT) / 2;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if (isOpen) {
            drawOpen();
        } else if (isOpening) {
            drawOpening();
        } else {
            drawCover();
        }
    }

    private void drawCover() {
        minX = (width - BOOK_COVER_WIDTH) / 2;
        minY = (height - BOOK_COVER_HEIGHT) / 2;

        Minecraft.getMinecraft().renderEngine.bindTexture(COVER);
        drawTexturedModalRect(minX, minY, BOOK_COVER_X, BOOK_COVER_Y, BOOK_COVER_WIDTH, BOOK_COVER_HEIGHT);
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
            drawTexturedModalRect(minX + BOOK_COVER_WIDTH - PAGE_RIGHT_WIDTH, minY, PAGE_RIGHT_X, PAGE_RIGHT_Y, PAGE_RIGHT_WIDTH, PAGE_RIGHT_HEIGHT);

            mc.renderEngine.bindTexture(COVER);
            drawScaledCustomSizeModalRect(minX, minY, BOOK_COVER_X, BOOK_COVER_Y, BOOK_COVER_WIDTH, BOOK_COVER_HEIGHT, coverWidth, BOOK_COVER_HEIGHT,
                    256, 256);

            drawScaledCustomSizeModalRect(minX + coverWidth, minY, BOOK_BINDING_X, BOOK_BINDING_Y, BOOK_BINDING_WIDTH, BOOK_BINDING_HEIGHT,
                    bindingWidth, BOOK_BINDING_HEIGHT, 256, 256);

        } else if (openingAngle == 0) {
            minX = (width - BOOK_COVER_WIDTH) / 2;
            minY = (height - BOOK_COVER_HEIGHT) / 2;

            mc.renderEngine.bindTexture(RIGHT_PAGE);
            drawTexturedModalRect(minX + BOOK_COVER_WIDTH - PAGE_LEFT_WIDTH, minY, PAGE_RIGHT_X, PAGE_RIGHT_Y, PAGE_RIGHT_WIDTH, PAGE_RIGHT_HEIGHT);

            mc.renderEngine.bindTexture(COVER);
            drawTexturedModalRect(minX, minY, BOOK_BINDING_X, BOOK_BINDING_Y, BOOK_BINDING_WIDTH, BOOK_BINDING_HEIGHT);
        } else if (openingAngle > 0) {
            int pageWidth = (int) (sin * PAGE_LEFT_WIDTH);
            int bindingWidth = (int) ((1 - sin) * BOOK_BINDING_WIDTH);

            int penHeight = (int) (sin * PEN_HIDDEN_HEIGHT_MIN);

            minX = (width - BOOK_COVER_WIDTH - pageWidth) / 2;
            minY = (height - BOOK_COVER_HEIGHT) / 2;

            mc.renderEngine.bindTexture(RIGHT_PAGE);
            drawTexturedModalRect(minX + pageWidth + bindingWidth, minY, PAGE_RIGHT_X, PAGE_RIGHT_Y, PAGE_RIGHT_WIDTH, PAGE_RIGHT_HEIGHT);

            mc.renderEngine.bindTexture(LEFT_PAGE);
            drawScaledCustomSizeModalRect(minX + bindingWidth, minY, PAGE_LEFT_X, PAGE_LEFT_Y, PAGE_LEFT_WIDTH, PAGE_LEFT_HEIGHT, pageWidth,
                    PAGE_LEFT_HEIGHT, 256, 256);

            mc.renderEngine.bindTexture(COVER);
            drawScaledCustomSizeModalRect(minX, minY, BOOK_BINDING_X, BOOK_BINDING_Y, BOOK_BINDING_WIDTH, BOOK_BINDING_HEIGHT, bindingWidth,
                    BOOK_BINDING_HEIGHT, 256, 256);

            mc.renderEngine.bindTexture(ICONS);
            drawTexturedModalRect(minX + pageWidth + bindingWidth - (PEN_HIDDEN_WIDTH / 2), minY - penHeight, PEN_HIDDEN_X, PEN_HIDDEN_Y,
                    PEN_HIDDEN_WIDTH, penHeight);
        }
    }

    private void drawOpen() {
        mc.renderEngine.bindTexture(LEFT_PAGE);
        drawTexturedModalRect(minX, minY, PAGE_LEFT_X, PAGE_LEFT_Y, PAGE_LEFT_WIDTH, PAGE_LEFT_HEIGHT);

        mc.renderEngine.bindTexture(RIGHT_PAGE);
        drawTexturedModalRect(minX + PAGE_LEFT_WIDTH, minY, PAGE_RIGHT_X, PAGE_RIGHT_Y, PAGE_RIGHT_WIDTH, PAGE_RIGHT_HEIGHT);

        isOverHover = mouseX >= minX + PEN_HIDDEN_BOX_X_MIN && mouseX <= minX + PEN_HIDDEN_BOX_X_MAX && mouseY >= minY + PEN_HIDDEN_BOX_Y_MIN
            && mouseY <= minY + PEN_HIDDEN_BOX_Y_MAX;

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
