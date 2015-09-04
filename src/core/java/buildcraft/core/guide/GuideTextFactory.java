package buildcraft.core.guide;

import buildcraft.core.guide.node.TextNode;

public class GuideTextFactory extends GuidePartFactory<GuideText> {
    private final TextNode text;

    public GuideTextFactory(TextNode text) {
        this.text = text;
    }

    @Override
    public GuideText createNew() {
        return new GuideText(text);
    }
}
