package buildcraft.core.guide;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class GuideManager {
    static final Map<ModContainer, GuideManager> managers = Maps.newHashMap();

    /** A cache of what has been loaded so far by this guide. */
    private final Map<ResourceLocation, GuidePart> guideMap = Maps.newHashMap();
    /** All of the guide pages that have been registered to appear in this guide manager */
    private final Map<ResourceLocation, GuidePage> registeredLocations = Maps.newHashMap();
    /** Base locations for generic chapters */
    private final String locationBase, locationBlock, locationItem, locationMechanic;

    public GuideManager(String assetBase) {
        locationBase = assetBase + ":guide/";
        locationBlock = locationBase + "block/";
        locationItem = locationBase + "item/";
        locationMechanic = locationBase + "mechanic/";
    }

    public static void registerManager(GuideManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Tried to register a null manager!");
        }
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            throw new IllegalStateException("Tried to register a manager outside of a correct event!");
        }
        if (managers.containsKey(container)) {
            throw new IllegalStateException("Tried to register a manager twice for one mod!");
        }
        managers.put(container, manager);
    }

    public void registerCustomPage(ResourceLocation location, GuidePage page) {
        registeredLocations.put(location, page);
        guideMap.put(location, page);
    }

    public void registerPage(ResourceLocation location) {
        registerCustomPage(location, getPage(location.toString()));
    }

    private GuidePart getPart(ResourceLocation location) {
        if (guideMap.containsKey(location)) {
            return guideMap.get(location);
        }
        GuidePart part = null;
        if (location.getResourcePath().endsWith("md")) {// Wiki info page (Markdown)
            part = MarkdownLoader.loadMarkdown(location);
        } else if (location.getResourcePath().endsWith("png")) { // Image
            part = ImageLoader.loadImage(location);
        } else {
            throw new IllegalArgumentException("Recieved an unknown filetype! " + location);
        }
        System.out.println("Getting " + location + " for the first time...");
        guideMap.put(location, part);
        return part;
    }

    private GuidePage getPage(String locationBase) {
        return (GuidePage) getPart(new ResourceLocation(locationBase + ".md"));
    }

    public GuidePage getItemPage(Item item) {
        return getPage(locationItem + item.getUnlocalizedName());
    }

    public GuidePage getBlockPage(Block block) {
        return getPage(locationBlock + block.getUnlocalizedName());
    }

    public GuidePage getMechanicPage(String mechanic) {
        return getPage(locationMechanic + mechanic);
    }

    /** Gets an image for display that */
    public GuideImage getImage(String imageLocation) {
        return (GuideImage) getPart(new ResourceLocation(locationBase, imageLocation + ".png"));
    }

    public GuideRenderedBlock getBlockImage(IBlockState state) {
        return new GuideRenderedBlock(state);
    }

    public GuideRenderedItem getItemImage(ItemStack stack) {
        return new GuideRenderedItem(stack);
    }
}
