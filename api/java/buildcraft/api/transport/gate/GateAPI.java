package buildcraft.api.transport.gate;

import buildcraft.api.APIHelper;

public class GateAPI {
    public static final IGateRegistry REGISTRY;
    public static final IGateAddonRegistry ADDON_REGISTRY;

    public static final IGateBehaviourFactory AND_FACTORY;
    public static final IGateBehaviourFactory OR_FACTORY;

    static {
        REGISTRY = APIHelper.getInstance("buildcraft.transport.GateRegistry", IGateRegistry.class, VoidGateRegistry.INSTANCE);
        ADDON_REGISTRY = APIHelper.getInstance("buildcraft.transport.GateAddonRegistry", IGateAddonRegistry.class, VoidGateAddonRegistry.INSTANCE);

        AND_FACTORY = APIHelper.getGateBehaviourFactory("buildcraft.transport.");
        OR_FACTORY = APIHelper.getGateBehaviourFactory("buildcraft.transport.");
    }
}
