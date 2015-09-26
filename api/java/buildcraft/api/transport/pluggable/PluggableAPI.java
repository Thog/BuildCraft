package buildcraft.api.transport.pluggable;

import buildcraft.api.APIHelper;

public class PluggableAPI {
    public static final IPluggableRegistry REGISTRY;

    static {
        REGISTRY = APIHelper.getInstance("buildcraft.transport.PluggableRegistry", IPluggableRegistry.class, VoidPluggableRegistry.INSTANCE);
    }
}
