package buildcraft.core.guide;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import buildcraft.api.core.BCLog;
import buildcraft.core.guide.node.NodePageLine;

public class MarkdownLoader extends LocationLoader {
    public static GuidePartFactory<GuidePageBase> loadMarkdown(ResourceLocation location) {
        String[] lineArray = asString(location).split("\n");
        List<GuidePartFactory<?>> parts = Lists.newArrayList();

        NodePageLine currentNode = null;
        for (String line : lineArray) {
            // A link to something else. It might not be, but you never know
            if (line.startsWith("[")) {

            }

            // An image to something
            if (line.startsWith("![")) {

            }

            // Something special and custom to BuildCraft (not in standard markdown)
            if (line.startsWith("$[special.")) {
                line = line.substring("$[special.".length());
                if (line.startsWith("crafting]")) {
                    line = line.substring("crafting]".length());
                    // Recipe for a simple item
                    if (line.startsWith("(") && line.endsWith(")")) {
                        String itemText = line.substring(1, line.length() - 1);
                        Item item = Item.getByNameOrId(itemText);
                        if (item != null) {
                            GuideCraftingFactory factory = GuideCraftingFactory.create(item);
                            if (factory != null) {
                                currentNode = null;
                                parts.add(GuideCraftingFactory.create(item));
                                continue;
                            } else {
                                BCLog.logger.warn("Didn't find a recipe for " + item);
                                // Unwrap it to what it was before
                                line = "$[special.crafting]" + line;
                            }
                        } else {
                            BCLog.logger.warn("Didn't find an item for " + itemText);
                            // Unwrap it to what it was before
                            line = "$[special.crafting]" + line;
                        }
                    }
                    // Recipe for a complex item
                    else if (line.startsWith("{") && line.endsWith("}")) {

                    } else {
                        // Unwrap back what was there before.
                        line = "crafting](" + line;
                    }
                } else {
                    line = "$[special." + line;
                }
            }
            if (line.length() == 0) {
                line = " ";
            }

            // Just use it as a normal text line
            if (currentNode == null) {
                currentNode = new NodePageLine(null, null);
                parts.add(new GuideTextFactory(currentNode));
            }
            currentNode.addChild(new PageLine(0, line, false));
        }
        return new GuidePageFactory(parts);
    }
}
