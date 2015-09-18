package buildcraft.api.transport;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;

import buildcraft.api.transport.event.IPipeContents;

public class PipeAPI {
    /** The base pipe registry. Register all pipes into this, but only AFTER BuildCraft Transport has completed
     * pre-init. */
    public static IPipeRegistry registry;

    /** The number of stacks contained within a pipe. Will return 0 if this not an item pipe. */
    public static final PipeProperty<Integer> STACK_COUNT = PipeProperty.create("stack_count", 0);

    /** The number of items (count of all items contained within all stacks) in the pipe. Will return 0 if this is not
     * an item pipe. This is more expensive to compute than STACK_COUNT, so it is recommended to use
     * {@link #STACK_COUNT} instead. */
    public static final PipeProperty<Integer> ITEM_COUNT = PipeProperty.create("item_count", 0);

    /** All of the items inside the pipe. Will return an empty list if there are no items. WARNING! This may be a very
     * expensive call! */
    public static final PipeProperty<ImmutableList<IPipeContents>> CONTENTS = PipeProperty.create("items", ImmutableList.<IPipeContents> of());

    /** The amount (in mB) of fluid in this fluid pipe or 0 if this not a fluid pipe. */
    public static final PipeProperty<Integer> FLUID_AMOUNT = PipeProperty.create("fluid_amount", 0);

    /** The type of fluid in the pipe (may be null if the pipe contains no fluid), or null if this is not a fluid pipe.
     * Note that even if the pipe returns 0 for {@link #FLUID_AMOUNT}, this fluid might not be null, so DO NOT assume
     * this pipe contains fluid if this returns a non-null value! */
    public static final PipeProperty<Fluid> FLUID_TYPE = PipeProperty.create("fluid_type", null);

    /** The total amount of power contained within this pipe. */
    public static final PipeProperty<Double> POWER = PipeProperty.create("power", 0.0);

    /** How full the pipe is (between 0 and 100), or 0 if this pipe has no capacity. */
    public static final PipeProperty<Integer> PERCENT_FULL = PipeProperty.create("percent_full", 0);

    static {
        registry = new IPipeRegistry() {
            @Override
            public Item registerPipeDefinition(PipeDefinition definition) {
                return null;
            }

            @Override
            public Set<Entry<String, Pair<PipeDefinition, Item>>> getPipeDefinitions() {
                return Collections.emptySet();
            }

            @Override
            public PipeDefinition getDefinition(String uniqueTag) {
                return null;
            }

            @Override
            public Item getItem(PipeDefinition definition) {
                return null;
            }

            @Override
            public PipeDefinition getDefinition(Item item) {
                return null;
            }

            @Override
            public String getUniqueTag(Item item) {
                return null;
            }
        };
    }
}
