package buildcraft.transport.gates;

import java.util.Locale;
import java.util.Set;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import buildcraft.api.gates.IGateExpansion;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.gates.GateDefinition.GateLogic;
import buildcraft.transport.gates.GateDefinition.GateMaterial;
import buildcraft.transport.item.ItemGate;

public class GateMeshDefinition implements ItemMeshDefinition {
    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        GateLogic logic = ItemGate.getLogic(stack);
        GateMaterial material = ItemGate.getMaterial(stack);
        Set<IGateExpansion> expansions = ItemGate.getInstalledExpansions(stack);
        String suffix = logic.name().toLowerCase(Locale.ROOT) + "_" + material.name().toLowerCase(Locale.ROOT);
        for (IGateExpansion expansion : expansions) {
            suffix += "_" + expansion.getOverlayItem();
        }
        return new ModelResourceLocation("buildcrafttransport:items/gate/" + suffix);
    }
}
