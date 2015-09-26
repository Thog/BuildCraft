package buildcraft.api.transport;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.fluids.Fluid;

import buildcraft.api.APIHelper;
import buildcraft.api.transport.event.IPipeContents;

public class PipeAPI {
    /** The base pipe registry. Register all pipes into this. */
    public static final IPipeRegistry REGISTRY;

    /** The number of stacks contained within a pipe. Will return 0 if this not an item pipe. */
    public static final PipeProperty<Integer> STACK_COUNT;

    /** The number of items (count of all items contained within all stacks) in the pipe. Will return 0 if this is not
     * an item pipe. This is more expensive to compute than STACK_COUNT, so it is recommended to use
     * {@link #STACK_COUNT} instead. */
    public static final PipeProperty<Integer> ITEM_COUNT;

    /** All of the items inside the pipe. Will return an empty list if there are no items. WARNING! This may be a very
     * expensive call as it must query the pipe internals every tick that this is called! */
    public static final PipeProperty<ImmutableList<IPipeContents>> CONTENTS;

    /** The amount (in mB) of fluid in this fluid pipe or 0 if this not a fluid pipe. */
    public static final PipeProperty<Integer> FLUID_AMOUNT;

    /** The type of fluid in the pipe (may be null if the pipe contains no fluid), or null if this is not a fluid pipe.
     * Note that even if the pipe returns 0 for {@link #FLUID_AMOUNT}, this fluid might not be null, so DO NOT assume
     * this pipe contains fluid if this returns a non-null value! */
    public static final PipeProperty<Fluid> FLUID_TYPE;

    /** The total amount of power contained within this pipe. */
    public static final PipeProperty<Double> POWER;

    /** How full the pipe is (between 0 and 100), or 0 if this pipe has no capacity. */
    public static final PipeProperty<Integer> PERCENT_FULL;

    static {
        REGISTRY = APIHelper.getInstance("buildcraft.transport.PipeRegistry", IPipeRegistry.class, VoidPipeRegistry.INSTANCE);

        STACK_COUNT = PipeProperty.create("stack_count", 0);
        ITEM_COUNT = PipeProperty.create("item_count", 0);

        FLUID_AMOUNT = PipeProperty.create("fluid_amount", 0);
        FLUID_TYPE = PipeProperty.create("fluid_type", null);

        POWER = PipeProperty.create("power", 0.0);

        CONTENTS = PipeProperty.create("items", ImmutableList.<IPipeContents> of());
        PERCENT_FULL = PipeProperty.create("percent_full", 0);
    }
}
