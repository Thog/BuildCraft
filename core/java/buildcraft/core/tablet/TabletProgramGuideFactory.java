package buildcraft.core.tablet;

import buildcraft.api.tablet.ITablet;
import buildcraft.api.tablet.TabletBitmap;
import buildcraft.api.tablet.TabletProgram;
import buildcraft.api.tablet.TabletProgramFactory;

public class TabletProgramGuideFactory extends TabletProgramFactory {
    private final TabletBitmap icon = new TabletBitmap(32, 32);

    public TabletProgramGuideFactory() {

    }

    @Override
    public TabletProgram create(ITablet tablet) {
        return new TabletProgramGuide(tablet);
    }

    @Override
    public String getName() {
        return "guide";
    }

    @Override
    public TabletBitmap getIcon() {
        return icon;
    }

}
