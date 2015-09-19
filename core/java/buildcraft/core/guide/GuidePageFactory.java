package buildcraft.core.guide;

public class GuidePageFactory extends GuidePartFactory<GuidePage> {
    @Override
    public GuidePage createNew(GuiGuide gui) {
        return new GuidePage(gui);
    }
}
