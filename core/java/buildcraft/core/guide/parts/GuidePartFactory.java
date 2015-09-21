package buildcraft.core.guide.parts;

import buildcraft.core.guide.GuiGuide;

public abstract class GuidePartFactory<T extends GuidePart> {
    public abstract T createNew(GuiGuide gui);
}
