package buildcraft.core.guide;

import buildcraft.core.guide.node.TextNode;

public class GuideText extends GuidePart {
    private final TextNode text;

    public GuideText(TextNode text) {
        this.text = text;
    }

    @Override
    public void renderIntoArea(int x, int y, int width, int height) {

    }
}
