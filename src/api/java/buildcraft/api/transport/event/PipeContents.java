package buildcraft.api.transport.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class PipeContents {
    public static class PipeContentsItem extends PipeContents {
        public final ItemStack item;

        public PipeContentsItem(ItemStack item) {
            this.item = item;
        }
    }

    public static class PipeContentsFluid extends PipeContents {
        public final FluidStack fluid;

        public PipeContentsFluid(FluidStack fluid) {
            this.fluid = fluid;
        }
    }

    public static class PipeContentsPower extends PipeContents {
        public final double mj;

        public PipeContentsPower(double mj) {
            this.mj = mj;
        }
    }
}
