package buildcraft.transport;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipeBehaviourFactory;
import buildcraft.api.transport.PipeAPI;
import buildcraft.api.transport.PipeDefinition;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.pipes.BehaviourFactoryBasic;
import buildcraft.transport.pipes.BehaviourFactoryBasic.EnumListStatus;
import buildcraft.transport.pipes.BehaviourFactoryPolishedStone;
import buildcraft.transport.pipes.BehaviourFactoryWooden;
import buildcraft.transport.pipes.EnumPipeMaterial;

public class TransportItems {
    private static final Table<EnumPipeMaterial, EnumPipeType, PipeDefinition> pipes = HashBasedTable.create();

    public static void initItems() {
        // Register everything that applies to all different types
        for (EnumPipeType type : EnumPipeType.CONTENTS) {
            for (int i = 0; i < 4; i++) {
                // Don't set the factory as we will set it below
                EnumPipeMaterial material = EnumPipeMaterial.STONES[i * 2];
                // Create the unpolished variant
                registerDefinition(material, type, createBasicDefinition(type, material, false));

                material = EnumPipeMaterial.STONES[i * 2 + 1];
                // Create the polished variant
                registerDefinition(material, type, createPolishedStonesDefinition(type, material));
            }

            // Register blacklist for the unpolished stones
            {
                PipeDefinition[] unpolished = new PipeDefinition[4];
                BehaviourFactoryBasic[] unpolishedFactories = new BehaviourFactoryBasic[4];

                PipeDefinition[] polished = new PipeDefinition[4];
                BehaviourFactoryPolishedStone[] polishedFactories = new BehaviourFactoryPolishedStone[4];

                for (int i = 0; i < 4; i++) {
                    unpolished[i] = pipes.get(EnumPipeMaterial.STONES[i * 2], type);
                    unpolishedFactories[i] = (BehaviourFactoryBasic) unpolished[i].behaviourFactory;

                    polished[i] = pipes.get(EnumPipeMaterial.STONES[i * 2 + 1], type);
                    polishedFactories[i] = (BehaviourFactoryPolishedStone) polished[i].behaviourFactory;
                }

                for (int i = 0; i < 4; i++) {
                    List<PipeDefinition> otherUnpolished = Lists.newArrayList(unpolished);
                    otherUnpolished.remove(unpolished[i]);
                    unpolishedFactories[i].setDefinition(unpolished[i], EnumListStatus.BLACKLIST, otherUnpolished.toArray(new PipeDefinition[3]));

                    polishedFactories[i].setDefinition(polished[i], EnumListStatus.WHITELIST, unpolished[i], polished[i]);
                }
            }
            // Register wooden pipe as special
            registerDefinition(EnumPipeMaterial.WOOD, type, createWoodenDefinition(type));
        }

        PipeDefinition definition = createBasicDefinition(EnumPipeType.STRUCTURE, EnumPipeMaterial.COBBLESTONE, true);
        registerDefinition(EnumPipeMaterial.COBBLESTONE, EnumPipeType.STRUCTURE, definition);
    }

    public static void addRecipies() {
        // Setup the glass types for later
        ItemStack[] glassTypes = new ItemStack[17];
        glassTypes[0] = new ItemStack(Blocks.glass);
        for (int i = 0; i < 16; i++) {
            glassTypes[i + 1] = new ItemStack(Blocks.stained_glass, 1, i);
        }

        // For every registered pipe
        for (Cell<EnumPipeMaterial, EnumPipeType, PipeDefinition> entry : pipes.cellSet()) {
            EnumPipeMaterial material = entry.getRowKey();
            EnumPipeType type = entry.getColumnKey();
            PipeDefinition definition = entry.getValue();
            Item pipe = PipeAPI.registry.getItem(definition);

            // 0 for unpainted and 1-16 for different glass colours
            for (int i = 0; i < 17; i++) {
                ItemStack output = new ItemStack(pipe, 1, i);

                if (type == EnumPipeType.ITEM) {
                    ItemStack in1 = material.ingredient1;
                    ItemStack in2 = material.ingredient2;
                    GameRegistry.addShapedRecipe(output, "1G2", '1', in1, 'G', glassTypes[i], '2', in2);
                } else {
                    // Fluid and power use modifiers on the item pipe
                    ItemStack modifier;
                    if (type == EnumPipeType.FLUID) {
                        modifier = new ItemStack(BuildCraftTransport.pipeWaterproof);
                    } else {
                        modifier = new ItemStack(Items.redstone);
                    }
                    Item pipeItem = null;
                    ItemStack inputPipe = new ItemStack(pipeItem, 1, i);
                    GameRegistry.addShapelessRecipe(output, modifier, inputPipe);

                    // Also allow uncrafting fluid + power pipes back down to item pipes, losing the modifier in the
                    // process

                    GameRegistry.addShapelessRecipe(inputPipe, output);
                }
            }
        }
    }

    public static PipeDefinition getPipe(EnumPipeType type, EnumPipeMaterial material) {
        return pipes.get(material, type);
    }

    private static PipeDefinition createBasicDefinition(EnumPipeType type, EnumPipeMaterial material, boolean defineFactoryFully) {
        BehaviourFactoryBasic factory = new BehaviourFactoryBasic();
        PipeDefinition definition = createDefinition(type, material, factory);
        if (defineFactoryFully) {
            factory.setDefinition(definition, EnumListStatus.BLACKLIST);
        }
        return definition;
    }

    private static PipeDefinition createPolishedStonesDefinition(EnumPipeType type, EnumPipeMaterial material) {
        BehaviourFactoryPolishedStone factory = new BehaviourFactoryPolishedStone();
        PipeDefinition definition = createDefinition(type, material, factory);
        return definition;
    }

    private static PipeDefinition createWoodenDefinition(EnumPipeType type) {
        BehaviourFactoryWooden factory = new BehaviourFactoryWooden();
        PipeDefinition definition = createDefinition(type, EnumPipeMaterial.WOOD, factory);
        factory.setDefinition(definition);
        return definition;
    }

    private static PipeDefinition createDefinition(EnumPipeType type, EnumPipeMaterial material, IPipeBehaviourFactory factory) {
        String name = material.name().toLowerCase(Locale.ROOT) + "_" + type.name().toLowerCase(Locale.ROOT);
        PipeDefinition definition = new PipeDefinition(name, type, material.maxSprites, "buildcrafttransport:pipes/", factory);
        return definition;
    }

    private static void registerDefinition(EnumPipeMaterial material, EnumPipeType type, PipeDefinition definition) {
        Item item = PipeAPI.registry.registerPipeDefinition(definition);
        item.setUnlocalizedName("buildcraft_" + item.getUnlocalizedName().replace("item.", ""));
        pipes.put(material, type, definition);
        CoreProxy.proxy.registerItem(item);
    }
}
