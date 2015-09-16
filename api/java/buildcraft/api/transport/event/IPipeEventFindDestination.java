package buildcraft.api.transport.event;

import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IPipeEventFindDestination extends IPipeEvent {
    /** @return The current contents of the pipe */
    IPipeContents getContents();

    /** @return Where the contents came from. Note that {@link #getPotentialDestinations()} will never contain this as a
     *         key. */
    EnumFacing getOrigin();

    /** @return An immutable map of faces the contents can go to. The values are the tile entity that they connect
     *         to. */
    ImmutableMap<EnumFacing, TileEntity> getPotentialDestinations();

    /** @return The current list of destinations that will be selected from at random. Note that this set is
     *         unmodifiable, use {@link #addDestination(EnumFacing)} to add a destination. */
    Set<EnumFacing> getDestinations();

    /** Add a destination if it does not already exist, provided:
     * <ul>
     * <li>The destination is not null, AND</li>
     * <li>The destination is a key within {@link #getPotentialDestinations()}, AND</li>
     * <li>The</li>
     * </ul>
     * 
     * @param destination The destination to add.
     * @throws IllegalArgumentException if the destination violated any of the above rules */
    void addDestination(EnumFacing destination) throws IllegalArgumentException;

    /** Add all the destinations according to rules given by {@link #addDestination(EnumFacing)}.
     * 
     * @param destinations The destinations to add
     * @throws IllegalArgumentException if any of the destinations violated any of the given rules */
    void addDestinations(EnumFacing... destinations) throws IllegalArgumentException;

    /** Add all the destinations according to the rules given by {@link #addDestination(EnumFacing)}
     * 
     * @param destinations The destinations to add
     * @throws IllegalArgumentException if any of the destinations violated any of the given rules */
    void addDestinations(Iterable<EnumFacing> destinations) throws IllegalArgumentException;

    /** Add all of the possible destinations that fit the rules given by {@link #addDestination(EnumFacing)}. Note that
     * the resulting set of destinations will equal the key set given by {@link #getPotentialDestinations()} */
    void addAllPossibleDestinations();

    /** Removes a possible destination if it is in the {@link #getDestinations()} set
     * 
     * @param destination The destination to remove */
    void removeDestination(EnumFacing destination);

    /** Removes all of the destinations if it is in the {@link #getDestinations()} set
     * 
     * @param destinations The destinations to remove */
    void removeDestinations(EnumFacing... destinations);

    /** Removes all of the destinations if it is in the {@link #getDestinations()} set
     * 
     * @param destinations The destinations to remove */
    void removeDestinations(Iterable<EnumFacing> destinations);

    /** Removes all of the destinations currently contained within {@link #getDestinations()}. Note that if no other
     * destinations are added then the item (if it is an item) will be dropped, power or fluids will stop flowing */
    void clearDestinations();

    /** @return The maximum possible number of destinations that can be accessed. */
    int getMaxPossibleDestinations();

    /** @return The maximum number of destinations that will be chosen. Is guaranteed to be less than or equal to
     *         {@link #getMaxPossibleDestinations()} */
    int getMaxDestinations();

    /** Set the maximum number of destinations that will be chosen.
     * 
     * @throws IllegalArgumentException if this number is greater than {@link #getMaxPossibleDestinations()} */
    void setMaxDestinations(int destinations) throws IllegalArgumentException;
}
