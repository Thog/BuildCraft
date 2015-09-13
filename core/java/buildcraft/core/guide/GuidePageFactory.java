package buildcraft.core.guide;

public class GuidePageFactory extends GuidePartFactory<GuidePage> {

    @Override
    public GuidePage createNew() {
        return new GuidePage();
    }

}
