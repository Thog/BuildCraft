package buildcraft.core.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;

import buildcraft.api.core.BCLog;

/** The base menu for showing all the locations. Should never be registered with and guide managers, this is special and
 * controls them all. */
public class GuideMenu extends GuidePage {
    /** Map of type (block, item, etc) -> List of pages for each (Quarry, Paintbrush, etc...) */
    private final Map<String, List<ResourceLocation>> contents = Maps.newHashMap();
    private final Map<ResourceLocation, PageMeta> metaMap = Maps.newHashMap();

    // TODO: Allow for expansions (plus-minus box to exapnd like a folder)
    private final List<PageLine> lines = Lists.newArrayList();

    public GuideMenu() {
        // Include all the mods
        for (GuideManager guide : GuideManager.managers.values()) {
            for (ResourceLocation location : guide.registeredPages.keySet()) {
                // Split the location to find the type
                String type = location.getResourcePath();
                if (!type.contains("/")) {
                    BCLog.logger.warn("The location " + location + " did not contain any folder seperators! This is a bug!");
                } else {
                    if (type.startsWith("guide/")) {
                        type = type.substring("guide/".length());
                    }
                    type = type.substring(0, type.indexOf("/"));
                    if (!contents.containsKey(type)) {
                        contents.put(type, new ArrayList<ResourceLocation>());
                    }
                    contents.get(type).add(location);
                    metaMap.put(location, guide.getPageMeta(location));
                }
            }
        }

        for (Entry<String, List<ResourceLocation>> entry : contents.entrySet()) {
            String key = entry.getKey();
            lines.add(new PageLine(null, 0, entry.getKey()));
            for (ResourceLocation location : entry.getValue()) {
                PageMeta meta = metaMap.get(location);
                String text = (meta == null) ? location.getResourcePath() : meta.title;
                lines.add(new PageLine(null, 1, text));
            }
        }
    }

    @Override
    protected void renderPage(int x, int y, int width, int height, int index) {
        PagePart part = new PagePart(0, 0);
        for (PageLine pageLine : lines) {
            part = renderLine(part, part, pageLine, x, y, width, height, part.page != index);
            if (part.page > index) {
                return;
            }
        }

        // PagePart part = new PagePart(0, 0);
        // PagePart current = part;
        // int lineIndex = 0;
        // while (part.page < index) {
        // PageLine line = lines.get(lineIndex);
        // PagePart newPart = renderLine(part, current, line, x, y, width, height, true);
        // if (part.page != newPart.page) {
        //
        // }
        // lineIndex++;
        // }
        // while (part.page == index) {
        // PageLine line = lines.get(lineIndex);
        // part = renderLine(part, part, line, x, y, width, height, false);
        // lineIndex++;
        // }
    }

    @Override
    public void handleMouseClick(int x, int y, int button, int[] arguments) {

    }
}
