package buildcraft.builders.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import buildcraft.core.blueprints.Blueprint;
import buildcraft.core.blueprints.BlueprintBase;
import buildcraft.core.blueprints.Template;
import buildcraft.core.gui.GuiTexture.GuiIcon;
import buildcraft.core.gui.GuiTexture.Rectangle;
import buildcraft.core.lib.world.FakeWorld;
import buildcraft.core.lib.world.FakeWorldManager;

public abstract class GuiBlueprintBase extends GuiScreen {
    public final GuiIcon BACKGROUND = new GuiIcon(getGuiBackground(), 0, 0, 200, 120);
    public final Rectangle PREVIEW = new Rectangle(30, 30, 140, 80);

    protected final BlueprintBase blueprint;
    private final FakeWorldManager fakeWorld;
    private float scroll = 64;

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

    protected abstract ResourceLocation getGuiBackground();

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // BACKGROUND.draw(minX, minY);
        // String text = blueprint.id.name;
        // int x = (width - fontRendererObj.getStringWidth(text)) / 2;
        // fontRendererObj.drawString(text, x, minY + 10, 0xFFFFFF);
        fakeWorld.renderWorld(width / 2, height / 2, mouseX, mouseY, scroll);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        scroll += Mouse.getEventDWheel() / 8f;
        if (scroll < 8) {
            scroll = 8;
        }
    }
}
