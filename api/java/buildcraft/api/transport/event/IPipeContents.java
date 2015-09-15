package buildcraft.api.transport.event;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public interface IPipeContents {
    void removeAll();

    public interface IPipeContentsItem extends IPipeContents {
        ItemStack getStack();

        void setStack();

        EnumDyeColor getColor();

        void setColor(EnumDyeColor color);
    }

    public interface IPipeContentsFluid extends IPipeContents {
        int getAmount();

        void setAmount(int amount);

        Fluid getFluid();

        void setFluid(Fluid fluid);
    }

    public interface IPipeContentsPower extends IPipeContents {
        double getPower();

        void setPower(double mj);
    }
}
