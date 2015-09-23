package buildcraft.builders.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import buildcraft.core.blueprints.BlueprintBase;

public abstract class GuiBlueprintBase extends GuiScreen {
    protected final BlueprintBase blueprint;

    public GuiBlueprintBase(BlueprintBase blueprint) {
        this.blueprint = blueprint;
    }

    protected abstract ResourceLocation getGuiBackground();
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
