package buildcraft.api.transport;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;

public interface IPipeRegistry {
    /** Registers a pipe definition, returning the item associated with it. Note that the item has not been registered,
     * so you still need to register it with forge. */
    Item registerPipeDefinition(PipeDefinition definition);

    /** @return An unmodifiable set containing all the current mappings of the registry */
    Set<Entry<String, Pair<PipeDefinition, Item>>> getPipeDefinitions();

    // Specifics

    // String -> Definition - Needed, base requirment
    /** @param uniqueTag The tag to look up. Can be null.
     * @return The unique definition associated with the unique tag, or null if the tag was not mapped */
    PipeDefinition getDefinition(String uniqueTag);

    // String -> Item - NOT REQUIRED: getItem(getDefiniton()) will never fail, but it might return null

    // Definition -> Item - Needed, no other way (The definition does not require the
    /** @param definition The pipe definition to look up. Can be null.
     * @return The item the has been mapped to the definition, or null if the definition was not mapped */
    Item getItem(PipeDefinition definition);

    // Item -> Definition
    /** @param item The item to look up the definition of. Can be null.
     * @return The definition associated with that item, or null if the item was not mapped. */
    PipeDefinition getDefinition(Item item);

    // Definition -> String (IMPLEMENTED- Definition.uniqueTag)

    /* Item -> String - QOL, if there's no definition for a string getDefinition().uniqueTag will throw an NPE if
     * there's no mapping */
    /** @param item The item to look up the tag of. Can be null.
     * @return The unique tag associated with that item, or null if the item was not mapped. */
    String getUniqueTag(Item item);
}
