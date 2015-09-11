package buildcraft.api.transport.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class PipeContents {
    public static class Item extends PipeContents {
        public ItemStack item;

        public Item(ItemStack item) {
            this.item = item;
        }

        @Override
        public void removeAll() {
            item.stackSize = 0;
        }
    }

    public static class PipeContentsFluid extends PipeContents {
        public FluidStack fluid;

        public PipeContentsFluid(FluidStack fluid) {
            this.fluid = fluid;
        }

        @Override
        public void removeAll() {
            fluid.amount = 0;
        }
    }

    public static class Power extends PipeContents {
        public double mj;

        public Power(double mj) {
            this.mj = mj;
        }

        @Override
        public void removeAll() {
            mj = 0;
        }
    }

    public abstract void removeAll();
}
