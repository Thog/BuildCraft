/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core;

public enum EnumGui {
    ARCHITECT_TABLE,
    BUILDER,
    FILLER,
    BLUEPRINT_LIBRARY,
    URBANIST,
    MAP,
    REQUESTER,
    LIST,
    TABLET,
    GUIDE,
    ENGINE_IRON,
    ENGINE_STONE,
    AUTO_CRAFTING_TABLE,
    REFINERY,
    CHUTE,
    ASSEMBLY_TABLE,
    PIPE_DIAMOND,
    GATES,
    PIPE_EMERALD_ITEM,
    PIPE_EMZULI_ITEM,
    PIPE_EMERALD_FLUID,
    FILTERED_BUFFER,

    /** Special value to indicate that this was not the correct value passed around */
    INVALID;

    public final int ID = ordinal();

    public static EnumGui from(int index) {
        if (index < 0 || index >= INVALID.ordinal()) {
            return INVALID;
        }
        return values()[index];
    }

}
