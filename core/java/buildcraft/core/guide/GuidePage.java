package buildcraft.core.guide;

import net.minecraft.client.gui.Gui;

import buildcraft.core.gui.GuiTexture.GuiIcon;

public class GuidePage extends GuidePart {
    public static final int LINE_HEIGHT = 16;
    public static final int INDENT_WIDTH = 16;

    /** The current page that is being rendered */
    private int index = 0;
    protected int numPages = -1;

    /** Stores information about the current rendering position */
    public static class PagePart {
        public final int page;
        public final int line;

        public PagePart(int page, int height) {
            this.page = page;
            this.line = height;
        }

        public PagePart nextLine(int by, int maxLines) {
            if (line + by >= maxLines) {
                return nextPage();
            }
            return new PagePart(page, line + by);
        }

        public PagePart nextPage() {
            return new PagePart(page + 1, 0);
        }
    }

    GuidePage(GuiGuide gui) {
        super(gui);
    }

    protected final int getIndex() {
        return index;
    }

    protected final void nextPage() {
        if (index + 1 < numPages) {
            index += 2;
        }
    }

    protected final void backPage() {
        index -= 2;
        if (index < 0) {
            index = 0;
        }
    }

    public int getPage() {
        return index;
    }

    public void tick(float elapsedTime) {}

    @Override
    public final void renderIntoArea(int x, int y, int width, int height) {
        // NO-OP
        // Instead render pages like below
    }

    private boolean wasHovered = false;

    /** @param start Where to start the rendering from.
     * @param current The current location of the rendering. This will be different from start if this line needed to
     *            render over 2 (or more!) pages
     * @param line The line to render
     * @param x The x position the page rendering started from
     * @param y The y position the page rendering started from
     * @param width The width of rendering space available
     * @param height The height of rendering space available
     * @param simulate If true, this will just calculate the positions and return without rendering.
     * @return The position for the next line to render at. Will automatically be the next page or line if necessary. */
    protected PagePart renderLine(PagePart start, PagePart current, PageLine line, int x, int y, int width, int height, int pageRenderIndex) {
        wasHovered = false;
        // Firstly break off the last chunk if the total length is greater than the width allows
        int allowedWidth = width - INDENT_WIDTH * line.indent;
        if (allowedWidth <= 0) {
            throw new IllegalStateException("Was indented too far");
        }

        int allowedLines = height / LINE_HEIGHT;

        String toRender = line.text;
        while (current.line <= allowedLines) {
            if (toRender.length() == 0) {
                break;
            }

            // Find out the longest string we can render
            int textLength = 1;
            // Start at 1 otherwise it will sometimes fail if there wasn't enough room
            while (textLength < toRender.length()) {
                String substring = toRender.substring(0, textLength);
                int textWidth = fontRenderer.getStringWidth(substring);
                if (textWidth > allowedWidth) {
                    break;
                }
                textLength++;
            }

            String thisLine = toRender.substring(0, textLength);
            toRender = toRender.substring(textLength);
            boolean render = pageRenderIndex == current.page;
            int stringWidth = fontRenderer.getStringWidth(thisLine);
            int linkX = x + INDENT_WIDTH * line.indent;
            int linkY = y + current.line * LINE_HEIGHT;
            int linkXEnd = linkX + stringWidth + 2;
            int linkYEnd = linkY + fontRenderer.FONT_HEIGHT + 2;
            if (line.link && mouseX >= linkX && mouseX <= linkXEnd && mouseY >= linkY && mouseY <= linkYEnd) {
                wasHovered = true;
                if (render) {
                    Gui.drawRect(linkX - 2, linkY - 2, linkXEnd, linkYEnd, 0xFFD3AD6C);
                }
            }
            if (render) {
                fontRenderer.drawString(thisLine, linkX, linkY, 0);
            }
            current = current.nextLine(1, allowedLines);
        }
        return current;
    }

    protected void renderLines(Iterable<PageLine> lines, int x, int y, int width, int height, int index) {
        PagePart part = new PagePart(0, 0);
        for (PageLine line : lines) {
            part = renderLine(part, part, line, x, y, width, height, index);
            if (part.page > index) {
                return;
            }
        }
    }

    protected PageLine getClicked(Iterable<PageLine> lines, int x, int y, int width, int height, int mouseX, int mouseY, int index) {
        PagePart part = new PagePart(0, 0);
        for (PageLine line : lines) {
            part = renderLine(part, part, line, x, y, width, height, -1);
            if (wasHovered) {
                return line;
            }
            if (part.page > index) {
                return null;
            }
        }
        return null;
    }

    public void renderFirstPage(int x, int y, int width, int height) {
        renderPage(x, y, width, height, index);
    }

    public void renderSecondPage(int x, int y, int width, int height) {
        renderPage(x, y, width, height, index + 1);
    }

    protected void renderPage(int x, int y, int width, int height, int index) {
        // Even => first page, draw page back button and first page index
        if (index % 2 == 0) {
            // Back page button
            if (index != 0) {
                GuiIcon icon = GuiGuide.TURN_BACK;
                if (icon.isMouseInside(x, y + height, mouseX, mouseY)) {
                    icon = GuiGuide.TURN_BACK_HOVERED;
                }
                icon.draw(x, y + height);
            }
            // Page index
            String text = (index + 1) + " / " + numPages;
            fontRenderer.drawString(text, x + GuiGuide.PAGE_LEFT_TEXT.width / 2 - fontRenderer.getStringWidth(text) / 2, y + height, 0);
        }
        // Odd => second page, draw forward button and second page index
        else {
            // Back page button
            if (index + 1 < numPages) {
                GuiIcon icon = GuiGuide.TURN_FORWARDS;
                if (icon.isMouseInside(x + width - icon.width, y + height, mouseX, mouseY)) {
                    icon = GuiGuide.TURN_FORWARDS_HOVERED;
                }
                icon.draw(x + width - icon.width, y + height);
            }
            // Page index
            if (index + 1 <= numPages) {
                String text = (index + 1) + " / " + numPages;
                fontRenderer.drawString(text, x + (GuiGuide.PAGE_RIGHT_TEXT.width - fontRenderer.getStringWidth(text)) / 2, y + height, 0);
            }
        }
    }

    @Override
    public final void handleMouseClick(int x, int y, int button, int... arguments) {
        // NO-OP, use the below!
    }

    protected void handleMouseClick(int x, int y, int width, int height, int mouseX, int mouseY, int mouseButton, int index, boolean isEditing) {
        // Even => first page, test page back button
        if (index % 2 == 0) {
            if (index != 0) {
                GuiIcon icon = GuiGuide.TURN_BACK;
                if (icon.isMouseInside(x, y + height, mouseX, mouseY)) {
                    backPage();
                }
            }
        }
        // Odd => second page, test forward page button
        else {
            if (index + 1 < numPages) {
                GuiIcon icon = GuiGuide.TURN_FORWARDS;
                if (icon.isMouseInside(x + width - icon.width, y + height, mouseX, mouseY)) {
                    nextPage();
                }
            }
        }
    }

    @Override
    public final void handleMouseDragPartial(int startX, int startY, int currentX, int currentY, int button) {

    }

    @Override
    public final void handleMouseDragFinish(int startX, int startY, int endX, int endY, int button) {

    }
}
