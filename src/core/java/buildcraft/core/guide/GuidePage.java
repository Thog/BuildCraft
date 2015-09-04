package buildcraft.core.guide;

import buildcraft.core.gui.GuiTexture.GuiIcon;

public class GuidePage extends GuidePart {
    public static final int LINE_HEIGHT = 16;
    public static final int INDENT_WIDTH = 16;

    /** The current page that is being rendered */
    private int index = 0;

    /** Stores information about a single line of text. This may be displayed as more than a single line though. */
    public class PageLine {
        /** Can be any of the boxes, any icon with dimensions different to these will render incorrectly */
        public final GuiIcon startIcon;
        public final int indent;
        /** This will be wrapped automatically when it is rendered. */
        public final String text;

        public PageLine(GuiIcon startIcon, int indent, String text) {
            this.startIcon = startIcon;
            this.indent = indent;
            this.text = text;
        }
    }

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

    public void tick(float elapsedTime) {}

    @Override
    public final void renderIntoArea(int x, int y, int width, int height) {
        // NO-OP
        // Instead render pages like below
    }

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
    protected PagePart renderLine(PagePart start, PagePart current, PageLine line, int x, int y, int width, int height, boolean simulate) {
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

            int page = current.page - start.page;

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
            if (page == 0 && !simulate) {
                fontRenderer.drawString(thisLine, x + INDENT_WIDTH * line.indent, y + current.line * LINE_HEIGHT, 0);
            }
            current = current.nextLine(1, allowedLines);
        }

        return current;
    }

    public void renderFirstPage(int x, int y, int width, int height) {
        renderPage(x, y, width, height, index);
    }

    public void renderSecondPage(int x, int y, int width, int height) {
        renderPage(x, y, width, height, index + 1);
    }

    protected void renderPage(int x, int y, int width, int height, int index) {

    }
}
