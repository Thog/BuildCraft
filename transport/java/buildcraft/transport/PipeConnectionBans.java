/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport;

import java.nio.channels.Pipe;

/** Controls whether one type of pipe can connect to another. */
// TODO (PASS 0): Change this to use EnumPipeMaterial as then we can get rid of the silly classes
// EVEN BETTER REMOVE IT ENTIRELY! YAAAAAAAAAY!
@Deprecated
public final class PipeConnectionBans {

    @Deprecated
    public static boolean canPipesConnect(Class<? extends Pipe> type1, Class<? extends Pipe> type2) {
        return true;
    }
}
