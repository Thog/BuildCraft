package buildcraft.transport.internal.pipes;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsFluid;

class PipeContentsFluid implements IPipeContentsFluid {
    FluidStack fluidStack;

    PipeContentsFluid(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    @Override
    public EnumPipeType getType() {
        return EnumPipeType.FLUID;
    }

    @Override
    public int getAmount() {
        return fluidStack == null ? 0 : fluidStack.amount;
    }

    @Override
    public Fluid getFluid() {
        return fluidStack == null ? null : fluidStack.getFluid();
    }
}
