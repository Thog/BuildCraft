package buildcraft.transport.internal.pipes;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContents;
import buildcraft.api.transport.event.IPipeEventFindDestination;

class PipeEventFindDestination extends PipeEvent implements IPipeEventFindDestination {
    private final IPipeContents contents;
    private final EnumFacing origin;
    private final ImmutableMap<EnumFacing, TileEntity> potentialDestinations;
    private final Set<EnumFacing> destinations;
    private final Set<EnumFacing> unmodifiableDestinations;
    private final int maxPossibleDestinations;
    int maxDestinations;

    PipeEventFindDestination(IPipe pipe, IPipeContents contents, EnumFacing origin, ImmutableMap<EnumFacing, TileEntity> potentialDestinations,
            int maxPossibleDestinations) {
        super(pipe);
        this.contents = contents;
        this.origin = origin;
        this.potentialDestinations = potentialDestinations;
        this.destinations = Sets.newHashSet(potentialDestinations.keySet());
        this.unmodifiableDestinations = Collections.unmodifiableSet(destinations);
        this.maxPossibleDestinations = maxPossibleDestinations;
        this.maxDestinations = maxPossibleDestinations;
    }

    @Override
    public IPipeContents getContents() {
        return contents;
    }

    @Override
    public EnumFacing getOrigin() {
        return origin;
    }

    @Override
    public ImmutableMap<EnumFacing, TileEntity> getPotentialDestinations() {
        return potentialDestinations;
    }

    @Override
    public Set<EnumFacing> getDestinations() {
        return unmodifiableDestinations;
    }

    @Override
    public void addDestination(EnumFacing destination) throws IllegalArgumentException {
        if (destination == null) {
            throw new IllegalArgumentException("Cannot add a null destination!");
        }
        if (!getPotentialDestinations().containsKey(destination)) {
            throw new IllegalArgumentException(String.format("%s was not a valid destination!", destination));
        }
        destinations.add(destination);
    }

    @Override
    public void addDestinations(EnumFacing... destinations) throws IllegalArgumentException {
        for (EnumFacing destination : destinations) {
            addDestination(destination);
        }
    }

    @Override
    public void addDestinations(Iterable<EnumFacing> destinations) throws IllegalArgumentException {
        for (EnumFacing destination : destinations) {
            addDestination(destination);
        }
    }

    @Override
    public void addAllPossibleDestinations() {
        for (EnumFacing destination : potentialDestinations.keySet()) {
            addDestination(destination);
        }
    }

    @Override
    public void removeDestination(EnumFacing destination) {
        destinations.remove(destination);
    }

    @Override
    public void removeDestinations(EnumFacing... destinations) {
        for (EnumFacing destination : destinations) {
            removeDestination(destination);
        }
    }

    @Override
    public void removeDestinations(Iterable<EnumFacing> destinations) {
        for (EnumFacing destination : destinations) {
            removeDestination(destination);
        }
    }

    @Override
    public void clearDestinations() {
        destinations.clear();
    }

    @Override
    public int getMaxPossibleDestinations() {
        return maxPossibleDestinations;
    }

    @Override
    public int getMaxDestinations() {
        return maxDestinations;
    }

    @Override
    public void setMaxDestinations(int destinations) throws IllegalArgumentException {
        int max = getMaxPossibleDestinations();
        if (destinations > max) {
            throw new IllegalArgumentException(String.format("Tried to add %d destinations when the maximum is %d!", destinations, max));
        }
        maxDestinations = destinations;
    }
}
