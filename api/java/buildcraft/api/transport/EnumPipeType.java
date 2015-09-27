package buildcraft.api.transport;

import buildcraft.transport.PipeTransport;
import buildcraft.transport.PipeTransportStructure;
import buildcraft.transport.internal.pipes.PipeTransportFluids;
import buildcraft.transport.internal.pipes.PipeTransportItems;
import buildcraft.transport.internal.pipes.PipeTransportPower;

public enum EnumPipeType implements IPipeType {
    // TODO (JDK1.8): Convert these to lambda!
    ITEM(true, true, new ITransportFactory() {
        @Override
        public PipeTransport create() {
            return new PipeTransportItems();
        }
    }),
    POWER(true, false, new ITransportFactory() {
        @Override
        public PipeTransport create() {
            return new PipeTransportPower();
        }
    }),
    FLUID(true, true, new ITransportFactory() {
        @Override
        public PipeTransport create() {
            return new PipeTransportFluids();
        }
    }),
    STRUCTURE(false, false, new ITransportFactory() {
        @Override
        public PipeTransport create() {
            return new PipeTransportStructure();
        }
    });

    interface ITransportFactory {
        PipeTransport create();
    }

    /** An array of pipe types that carry something */
    public static final EnumPipeType[] CONTENTS = { ITEM, POWER, FLUID };

    /** True for {@link #ITEM}, {@link #FLUID} and {link #POWER} */
    public final boolean carriesSomething;
    /** True for {@link #ITEM} and {@link #FLUID} */
    public final boolean carriesDifferentThings;
    private final ITransportFactory factory;

    private EnumPipeType(boolean carriesSomething, boolean carriesDifferentThings, ITransportFactory factory) {
        this.carriesSomething = carriesSomething;
        this.carriesDifferentThings = carriesDifferentThings;
        this.factory = factory;
    }

    @Override
    public PipeTransport createTransport() {
        return factory.create();
    }
}
