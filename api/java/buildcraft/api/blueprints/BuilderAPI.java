/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License. Please check the contents of the license, which
 * should be located as "LICENSE.API" in the BuildCraft source code distribution. */
package buildcraft.api.blueprints;

import buildcraft.api.APIHelper;

public enum BuilderAPI {
    INSTANCE;

    public static final ISchematicRegistry SCHEMATIC_REGISTRY;
    public static final IBlueprintDeployer BLUEPRINT_DEPLOYER;

    public static final double BREAK_ENERGY = 16;
    public static final double BUILD_ENERGY = 24;

    static {
        SCHEMATIC_REGISTRY = APIHelper.getInstance("buildcraft.core.blueprints.SchematicRegistry", ISchematicRegistry.class, null);
        BLUEPRINT_DEPLOYER = APIHelper.getInstance("buildcraft.core.blueprints.BlueprintDeployer", IBlueprintDeployer.class, null);
    }
}
