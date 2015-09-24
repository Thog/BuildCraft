package buildcraft.builders.gui;

import net.minecraft.util.ResourceLocation;

import buildcraft.core.blueprints.BlueprintBase;

public class GuiBlueprint extends GuiBlueprintBase {
    public GuiBlueprint(BlueprintBase blueprint) {
        super(blueprint);
    }

    @Override
    protected ResourceLocation getGuiBackground() {
        return new ResourceLocation("buildcraftbuilders:gui/blueprint/blue");
    }
}
