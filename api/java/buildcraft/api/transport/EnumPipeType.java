package buildcraft.api.transport;

import buildcraft.api.APIHelper;

public enum EnumPipeType implements IPipeType {
    ITEM(true, true, 1, APIHelper.getInstance("buildcraft.transport.TransportFactoryItem", ITransportFactory.class,
            TransportFactoryStructure.INSTANCE)),
    POWER(true, false, 1, APIHelper.getInstance("buildcraft.transport.TransportFactoryPower", ITransportFactory.class,
            TransportFactoryStructure.INSTANCE)),
    FLUID(true, true, 1, APIHelper.getInstance("buildcraft.transport.TransportFactoryFluid", ITransportFactory.class,
            TransportFactoryStructure.INSTANCE)),
    STRUCTURE(false, false, 1, TransportFactoryStructure.INSTANCE);

    /** An array of pipe types that carry something */
    public static final EnumPipeType[] CONTENTS = { ITEM, POWER, FLUID };

    private final boolean carriesSomething;
    private final boolean carriesDifferentThings;
    private final ITransportFactory factory;
    private final float scalar;

    private EnumPipeType(boolean carriesSomething, boolean carriesDifferentThings, float scalar, ITransportFactory factory) {
        this.carriesSomething = carriesSomething;
        this.carriesDifferentThings = carriesDifferentThings;
        this.scalar = scalar;
        this.factory = factory;
    }

    @Override
    public PipeTransport createTransport(IPipeTile tile) {
        return factory.create(tile);
    }

    @Override
    public boolean carriesAnything() {
        return carriesSomething;
    }

    @Override
    public boolean carriesUniqueThings() {
        return carriesDifferentThings;
    }

    @Override
    public float normaliseSpeed(float raw) {
        return raw * scalar;
    }

    @Override
    public float denormaliseSpeed(float normalised) {
        return normalised / scalar;
    }
}
