package buildcraft.core.guide;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import buildcraft.api.core.BCLog;
import buildcraft.core.guide.block.IBlockGuidePageMapper;
import buildcraft.core.lib.utils.Utils;

public class GuideManager {
    static final Map<ModContainer, GuideManager> managers = Maps.newHashMap();

    /** A cache of what has been loaded so far by this guide. */
    private final Map<ResourceLocation, GuidePartFactory<?>> guideMap = Maps.newHashMap();
    /** All of the guide pages that have been registered to appear in this guide manager */
    final Map<ResourceLocation, GuidePartFactory<GuidePage>> registeredPages = Maps.newHashMap();
    final Map<ResourceLocation, PageMeta> pageMetas = Maps.newHashMap();
    /** Base locations for generic chapters */
    private final String locationBase, locationBlock, locationItem, locationEntity, locationMechanic;

    final Map<Block, IBlockGuidePageMapper> customMappers = Maps.newHashMap();

    public GuideManager(String assetBase) {
        locationBase = assetBase + ":guide/";
        locationBlock = locationBase + "block/";
        locationItem = locationBase + "item/";
        locationEntity = locationBase + "entity/";
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

    // Page Registration

    public void registerCustomPage(ResourceLocation location, GuidePartFactory<GuidePage> page) {
        registeredPages.put(location, page);
        guideMap.put(location, page);
        BCLog.logger.info("Registered " + location + " for " + locationBase);
    }

    public void registerPage(ResourceLocation location) {
        registerCustomPage(location, (GuidePartFactory<GuidePage>) getPartFactory(location));
    }

    // Registration

    public void registerPageWithTitle(ResourceLocation location, PageMeta meta) {
        registerPage(location);
        pageMetas.put(location, meta);
    }

    public void registerBlock(Block block) {
        ResourceLocation location = new ResourceLocation(locationBlock + Utils.getModSpecificNameForBlock(block) + ".md");
        PageMeta meta = new PageMeta(block.getLocalizedName(), "");
        registerPageWithTitle(location, meta);
    }

    public void registerCustomBlock(Block block, IBlockGuidePageMapper mapper) {
        customMappers.put(block, mapper);
        for (String page : mapper.getAllPossiblePages()) {
            registerPage(new ResourceLocation(locationBlock + page + ".md"));
        }
    }

    /** Automatically registers all blocks that the calling mod has registered. Use {@link #unregisterBlock(Block)} to
     * remove specific blocks you don't want added to the guide. */
    public void registerAllBlocks() {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            // This is a definite coding error, so crash in the dev environment rather than putting up a warning
            throw new IllegalStateException("Was called outside the scope of an active mod! This is not how this is meant to be used!");
        }
        String prefix = container.getModId();
        for (Object key : Block.blockRegistry.getKeys()) {
            if (key instanceof ResourceLocation) {
                ResourceLocation location = (ResourceLocation) key;
                String domain = location.getResourceDomain();
                if (domain.equalsIgnoreCase(prefix)) {
                    registerBlock((Block) Block.blockRegistry.getObject(key));
                    BCLog.logger.info("Automatically added " + location + " as a block for " + prefix);
                } else {
                    // BCLog.logger.info("Skiped " + location + " as it was not a block for " + prefix);
                }
            } else {
                BCLog.logger.info("Found a different key! (" + key.getClass() + ")");
            }
        }
    }

    public void registerItem(Item item) {
        ResourceLocation location = new ResourceLocation(locationItem + Utils.getModSpecificNameForItem(item) + ".md");
        PageMeta meta = new PageMeta(new ItemStack(item).getDisplayName(), "");
        registerPageWithTitle(location, meta);
    }

    public void registerAllItems(boolean andItemBlocks) {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            // This is a definite coding error, so crash in the dev environment rather than putting up a warning
            throw new IllegalStateException("Was called outside the scope of an active mod! This is not how this is meant to be used!");
        }
        String prefix = container.getModId();
        for (Object key : Item.itemRegistry.getKeys()) {
            if (Block.getBlockFromItem((Item) Item.itemRegistry.getObject(key)) != null && !andItemBlocks) {
                continue;
            }
            if (key instanceof ResourceLocation) {
                ResourceLocation location = (ResourceLocation) key;
                String domain = location.getResourceDomain();
                if (domain.equalsIgnoreCase(prefix)) {
                    registerItem((Item) Item.itemRegistry.getObject(key));
                    BCLog.logger.info("Automatically added " + location + " as an item for " + prefix);
                }
            } else {
                BCLog.logger.info("Found a different key! (" + key.getClass() + ")");
            }
        }
    }

    // Unregistering

    public void unregister(ResourceLocation location) {
        registeredPages.remove(location);
        guideMap.remove(location);
    }

    public void unregisterBlock(Block block) {
        unregister(new ResourceLocation(locationBlock + Utils.getModSpecificNameForBlock(block)));
    }

    public void unregisterItem(Item item) {
        unregister(new ResourceLocation(locationItem + Utils.getNameForItem(item)));
    }

    public void unregisterEntity(Entity entity) {
        unregister(new ResourceLocation(locationEntity + EntityList.getEntityString(entity)));
    }

    public void unregisterMechanic(String mechanic) {
        unregister(new ResourceLocation(locationEntity + mechanic));
    }

    // Part getters

    GuidePartFactory<?> getPartFactory(ResourceLocation location) {
        if (guideMap.containsKey(location)) {
            return guideMap.get(location);
        }
        GuidePartFactory<?> part = null;
        if (location.getResourcePath().endsWith("md")) {// Wiki info page (Markdown)
            part = MarkdownLoader.loadMarkdown(location, this);
        } else if (location.getResourcePath().endsWith("png")) { // Image
            part = ImageLoader.loadImage(location);
        } else {
            throw new IllegalArgumentException("Recieved an unknown filetype! " + location);
        }
        BCLog.logger.info("Getting " + location + " for the first time...");
        guideMap.put(location, part);
        return part;
    }

    public PageMeta getPageMeta(ResourceLocation location) {
        if (pageMetas.containsKey(location)) {
            return pageMetas.get(location);
        }
        ResourceLocation metaLoc = new ResourceLocation(location + ".json");
        PageMeta meta = PageMetaLoader.load(metaLoc);
        pageMetas.put(location, meta);
        return meta;
    }

    private GuidePart getPart(ResourceLocation location) {
        return getPartFactory(location).createNew();
    }

    private GuidePage getPage(String locationBase) {
        return (GuidePage) getPart(new ResourceLocation(locationBase + ".md"));
    }

    public GuidePage getItemPage(Item item) {
        return getPage(locationItem + Utils.getNameForItem(item));
    }

    public GuidePage getBlockPage(Block block) {
        return getPage(locationBlock + Utils.getNameForBlock(block));
    }

    public GuidePage getEntityPage(Entity entity) {
        return getPage(locationEntity + EntityList.getEntityString(entity));
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
