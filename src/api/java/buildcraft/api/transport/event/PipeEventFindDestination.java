package buildcraft.api.transport.event;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class PipeEventFindDestination extends PipeEvent {
    public final PipeContents contents;
    public final EnumFacing from;
    public final ImmutableMap<EnumFacing, TileEntity> potentialDestinations;
    /** Add or remove destinations from this set */
    public final Set<EnumFacing> destinations;
    public final int maxDestinations;

    public PipeEventFindDestination(PipeContents contents, EnumFacing from, Map<EnumFacing, TileEntity> potentialDestinations,
            Set<EnumFacing> destinations, int maxDestinations) {
        this.contents = contents;
        this.from = from;
        this.potentialDestinations = ImmutableMap.copyOf(potentialDestinations);
        this.destinations = destinations;
        this.maxDestinations = maxDestinations;
    }
}
