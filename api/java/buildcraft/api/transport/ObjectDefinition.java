package buildcraft.api.transport;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ObjectDefinition {
    /** A globally unique tag for the pipe */
    public final String globalUniqueTag;
    /** A mod unique tag for the pipe. WARNING: this should only be used to register other mod-unique things, such as
     * the pipe or gate item. For general use, use {@link #globalUniqueTag} */
    public final String modUniqueTag;

    protected static String getCurrentMod() {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            throw new IllegalStateException("Pipes MUST be registered inside a mod");
        }
        return container.getModId();
    }

    public ObjectDefinition(String tag) {
        this.globalUniqueTag = getCurrentMod() + ":" + tag;
        this.modUniqueTag = tag;
    }
}
