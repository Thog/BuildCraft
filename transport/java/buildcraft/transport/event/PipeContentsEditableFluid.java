package buildcraft.transport.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.transport.event.IPipeContentsEditable.IPipeContentsEditableFluid;

public class PipeContentsEditableFluid extends PipeContentsFluid implements IPipeContentsEditableFluid {
    public PipeContentsEditableFluid(FluidStack fluidStack) {
        super(fluidStack);
    }

    @Override
    public void removeAll() {
        fluidStack = null;
    }

    @Override
    public void setAmount(int amount) {
        if (fluidStack == null) {
            throw new IllegalArgumentException("Did not know the fluid type!");
        } else if (amount <= 0) {
            fluidStack = new FluidStack(fluidStack.getFluid(), 0);
        } else {
            fluidStack = new FluidStack(fluidStack.getFluid(), amount);
        }
    }

    @Override
    public void multiplyAmount(double scalar) {
        if (getAmount() == 0) {
            return;
        }
        int amount = getAmount();
        amount *= scalar;
        setAmount(amount);
    }

    @Override
    public void setFluid(Fluid fluid) {
        if (fluidStack == null) {
            fluidStack = new FluidStack(fluid, 0);
        } else if (fluid == null) {
            fluidStack = null;
        } else {
            fluidStack = new FluidStack(getFluid(), fluidStack.amount);
        }
    }

    @Override
    public void setNBT(NBTTagCompound nbt) throws IllegalArgumentException {
        if (fluidStack == null) {
            throw new IllegalArgumentException("Did not have a stack to set the NBT to!");
        } else {
            fluidStack.tag = nbt;
        }
    }
}
