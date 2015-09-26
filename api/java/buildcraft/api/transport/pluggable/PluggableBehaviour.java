package buildcraft.api.transport.pluggable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.INBTStoreable;
import buildcraft.api.core.ISerializable;
import buildcraft.api.transport.IPipe;

public abstract class PluggableBehaviour implements INBTStoreable, ISerializable {
    public final PluggableDefinition definition;
    public final IPipe pipe;
    public final EnumFacing side;

    @SideOnly(Side.CLIENT)
    private IPluggableStaticRenderer staticRenderer;

    @SideOnly(Side.CLIENT)
    private IPluggableDynamicRenderer dynamicRenderer;

    public PluggableBehaviour(PluggableDefinition definition, IPipe pipe, EnumFacing side) {
        this.definition = definition;
        this.pipe = pipe;
        this.side = side;
    }

    /** @return True if you want to disable any pipe connections where this pluggable is. */
    public boolean isBlocking() {
        return true;
    }

    /** @return True if this pluggable makes the pipe solid on its own side. The only example within buildcraft is a
     *         non-hollow facade. */
    public boolean isSolid() {
        return false;
    }

    /** @return The renderer that will render this pluggable into a block model, or null to not use a block model
     *         renderer */
    @SideOnly(Side.CLIENT)
    protected abstract IPluggableStaticRenderer createStaticRenderer();

    /** @return The renderer that will render this pluggable dynamically (every tick), or null to not use a dynamic
     *         renderer. */
    @SideOnly(Side.CLIENT)
    protected abstract IPluggableDynamicRenderer createDynamicRenderer();

    @SideOnly(Side.CLIENT)
    public final IPluggableStaticRenderer getStaticRenderer() {
        if (staticRenderer != null) {
            return staticRenderer;
        }
        staticRenderer = createStaticRenderer();
        if (staticRenderer == null) {
            staticRenderer = EmptyPluggableRenderer.INSTANCE;
        }
        return staticRenderer;
    }

    @SideOnly(Side.CLIENT)
    public final IPluggableDynamicRenderer getDynamicRenderer() {
        if (dynamicRenderer != null) {
            return dynamicRenderer;
        }
        dynamicRenderer = createDynamicRenderer();
        if (dynamicRenderer == null) {
            dynamicRenderer = EmptyPluggableRenderer.INSTANCE;
        }
        return dynamicRenderer;
    }
}
