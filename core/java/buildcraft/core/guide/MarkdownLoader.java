package buildcraft.core.guide;

import net.minecraft.util.ResourceLocation;

public class MarkdownLoader extends LocationLoader {
    public static GuidePartFactory<GuidePage> loadMarkdown(ResourceLocation location, GuideManager manager) {
        String[] lineArray = asString(location).split("\n");

        for (String line : lineArray) {

        }

        return new GuidePageFactory();
    }
}
