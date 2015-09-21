package buildcraft.core.tablet;

import buildcraft.api.tablet.ITablet;
import buildcraft.api.tablet.TabletProgram;
import buildcraft.core.guide.GuideMenu;
import buildcraft.core.guide.parts.GuidePageBase;

public class TabletProgramGuide extends TabletProgram {
    private final ITablet tablet;
    private float t = 0.0F;
    private GuidePageBase currentPage = new GuideMenu();

    public TabletProgramGuide(ITablet tablet) {
        this.tablet = tablet;
    }

    @Override
    public void tick(float time) {
        t += time;
        currentPage.tick(t);
    }
}
