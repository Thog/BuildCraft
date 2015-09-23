package buildcraft.builders.gui;

import net.minecraft.util.ResourceLocation;

import buildcraft.core.blueprints.BlueprintBase;

public class GuiTemplate extends GuiBlueprintBase {
    public GuiTemplate(BlueprintBase blueprint) {
        super(blueprint);
    }

    @Override
    protected ResourceLocation getGuiBackground() {
        return null;
    }
}
