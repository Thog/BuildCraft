package buildcraft.api.transport.event;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import buildcraft.api.transport.EnumPipeType;

public interface IPipeContents {
    /** @return The current type. Note that this will never be {@link EnumPipeType#STRUCTURE} */
    EnumPipeType getType();

    public interface IPipeContentsItem extends IPipeContents {
        /** @return A copy of the item stack. */
        ItemStack getStack();

        /** @return The current colour. May be null if this is not coloured. */
        EnumDyeColor getColor();
    }

    public interface IPipeContentsFluid extends IPipeContents {
        /** @return The amount of fluid. */
        int getAmount();

        /** @return The actual fluid type. May be null */
        Fluid getFluid();
    }

    public interface IPipeContentsPower extends IPipeContents {
        /** @return The amount of power. */
        double getPower();
    }
}
