package buildcraft.api.transport;

public interface IPipeType {
    PipeTransport createTransport(IPipeTile tile);

    /** @return True if this transport actually carries something. Structure is the only one within buildcraft that
     *         returns false. */
    boolean carriesAnything();

    /** @return True if this pipe can carry different, unique things, specifically contents that cannot be merged
     *         together (Only structure and power returns false) */
    boolean carriesUniqueThings();

    float normaliseSpeed(float raw);

    float denormaliseSpeed(float normalised);
}
