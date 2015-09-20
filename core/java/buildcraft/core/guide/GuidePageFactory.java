package buildcraft.core.guide;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GuidePageFactory extends GuidePartFactory<GuidePageBase> {
    private final ImmutableList<GuidePartFactory<?>> parts;

    GuidePageFactory(List<GuidePartFactory<?>> parts) {
        this.parts = ImmutableList.copyOf(parts);
    }

    @Override
    public GuidePage createNew(GuiGuide gui) {
        List<GuidePart> parts = Lists.newArrayList();
        for (GuidePartFactory<?> factory : this.parts) {
            parts.add(factory.createNew(gui));
        }
        return new GuidePage(gui, parts);
    }
}
