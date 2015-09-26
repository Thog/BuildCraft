package buildcraft.api;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import net.minecraftforge.fml.common.Loader;

import buildcraft.api.core.BCLog;
import buildcraft.api.transport.gate.IGateBehaviourFactory;
import buildcraft.api.transport.gate.VoidGateBehaviourFactory;

public class APIHelper {
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String clsName, Class<T> baseVersion, T nullVersion) {
        try {
            Class<?> cls = Class.forName(clsName);
            Object[] arr = cls.getEnumConstants();
            if (arr == null || arr.length != 1) {
                return fail(clsName, nullVersion);
            }

            Object obj = arr[0];
            if (baseVersion.isInstance(obj)) {
                return (T) obj;
            }
            return fail(clsName, nullVersion);
        } catch (ClassNotFoundException e) {
            return fail(clsName, nullVersion);
        }
    }

    public static IGateBehaviourFactory getGateBehaviourFactory(String cls) {
        return getInstance(cls, IGateBehaviourFactory.class, VoidGateBehaviourFactory.INSTANCE);
    }

    private static <M> M fail(String clsName, M failure) {
        String[] split = clsName.split("\\.");
        String module;
        if (split.length < 2) {
            BCLog.logger.warn("Tried and failed to get the module name from " + Arrays.toString(split) + " (" + clsName + ")!");
            module = "invalid";
        } else {
            module = split[1];
        }
        module = StringUtils.capitalize(module);
        String bcMod = "BuildCraft|" + module;
        if (Loader.isModLoaded(bcMod)) {
            BCLog.logger.warn("Failed to load the  " + clsName + " dispite the appropriate buildcraft module being installed (" + module + ")");
        }
        return failure;
    }
}
