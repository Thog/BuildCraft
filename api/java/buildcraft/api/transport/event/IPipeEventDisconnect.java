package buildcraft.api.transport.event;

/** Note that {@link #getConnectingTile()} may return null as it will already have been removed from the world. */
public interface IPipeEventDisconnect extends IPipeEventConnection {}
