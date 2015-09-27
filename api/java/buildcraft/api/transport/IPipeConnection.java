/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License. Please check the contents of the license, which
 * should be located as "LICENSE.API" in the BuildCraft source code distribution. */
package buildcraft.api.transport;

import net.minecraft.util.EnumFacing;

public interface IPipeConnection {
    enum ConnectOverride {
        CONNECT,
        DISCONNECT,
        DEFAULT
    }

    /** Allows you to hint at pipe connection logic.
     * 
     * Note that this is merely a hint to the pipe that you would like to connect, as the pipe can *always* disallow a
     * connection if it wishes (For example sandstone will never connect to anything that is not a buildcraft pipe, even
     * if you override the connection here).
     * 
     * If you specify DISCONNECT however then the pipe will not be asked if it wants to connect.
     *
     * @param type
     * @param with
     * @return CONNECT to force a connection, DISCONNECT to force no connection, and DEFAULT to let the pipe decide. */
    ConnectOverride overridePipeConnection(IPipeType type, EnumFacing with);
}
