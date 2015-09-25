package buildcraft.builders.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;

import buildcraft.core.blueprints.Blueprint;
import buildcraft.core.blueprints.BlueprintBase;
import buildcraft.core.blueprints.Template;
import buildcraft.core.lib.world.FakeWorld;
import buildcraft.core.lib.world.FakeWorldManager;

public abstract class GuiBlueprintBase extends GuiScreen {
    protected final BlueprintBase blueprint;
    private final FakeWorldManager fakeWorld;
    private float scroll = 16;

    public GuiBlueprintBase(BlueprintBase blueprint) {
        this.blueprint = blueprint;
        FakeWorld world;
        if (blueprint instanceof Blueprint) {
            world = new FakeWorld((Blueprint) blueprint);
        } else {
            world = new FakeWorld((Template) blueprint, Blocks.brick_block.getDefaultState());
        }
        fakeWorld = new FakeWorldManager(world);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        double scale = scroll;
        fakeWorld.renderWorld(width / 2, height / 2, mouseX, mouseY, scale);
        fontRendererObj.drawString("scroll = " + scroll + ", scale = " + scale, 10, 10, 0);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        double deltaWheel = Mouse.getEventDWheel() / 64;
        scroll -= deltaWheel;
        if (scroll < 2) {
            scroll += deltaWheel;
        }
    }
}
