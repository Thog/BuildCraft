package buildcraft.transport.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsFluid;

public class PipeContentsFluid implements IPipeContentsFluid {
    protected FluidStack fluidStack;

    public PipeContentsFluid(FluidStack fluidStack) {
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

    @Override
    public NBTTagCompound getNBT() {
        if (fluidStack == null) {
            return null;
        }
        if (fluidStack.tag == null) {
            return null;
        }
        return (NBTTagCompound) fluidStack.tag.copy();
    }

    @Override
    public FluidStack cloneFluidStack() {
        FluidStack stack = fluidStack.copy();
        stack.tag = getNBT();
        return stack;
    }
}
