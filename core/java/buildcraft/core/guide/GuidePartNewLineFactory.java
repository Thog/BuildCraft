package buildcraft.core.guide;

import buildcraft.core.guide.GuidePartNewLineFactory.GuidePartNewLine;

public class GuidePartNewLineFactory extends GuidePartFactory<GuidePartNewLine> {
    public static class GuidePartNewLine extends GuidePart {
        public GuidePartNewLine(GuiGuide gui) {
            super(gui);
        }

        @Override
        public PagePart renderIntoArea(int x, int y, int width, int height, PagePart current, int index) {
            return current.newPage();
        }
    }

    @Override
    public GuidePartNewLine createNew(GuiGuide gui) {
        return new GuidePartNewLine(gui);
    }

}
