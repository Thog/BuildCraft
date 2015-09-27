package buildcraft.api.transport;

public class ObjectDefinitionFactory {
    public static ObjectDefinition create(String mod, String modUniqueTag) {
        return new ObjectDefinition(mod, modUniqueTag);
    }
}
