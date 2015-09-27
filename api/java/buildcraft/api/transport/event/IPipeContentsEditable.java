package buildcraft.api.transport.event;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

public interface IPipeContentsEditable extends IPipeContents {
    /** Removes all of the contents of this pipe. */
    void removeAll();

    public interface IPipeContentsEditableItem extends IPipeContentsEditable, IPipeContentsItem {
        /** @param stack The stack to set. */
        void setStack(ItemStack stack);

        /** @param color The colour to set. May be null if this item is not coloured. */
        void setColor(EnumDyeColor color);
    }

    public interface IPipeContentsEditableFluid extends IPipeContentsEditable, IPipeContentsFluid {
        /** @param amount The amount of fluid that is stored now
         * @throws IllegalArgumentException If {@link #getFluid()} returns null. (We cannot initialise a fluidstack with
         *             a null fluid as that will lead to all sorts of bugs- call {@link #setFluid(Fluid)} with a non
         *             null before if the {@link #getFluid()} returns null) */
        void setAmount(int amount) throws IllegalArgumentException;

        /** Note that this will NOT throw an {@link IllegalArgumentException} if the fluid is null- 0 times by anything
         * is still 0 so it doesn't matter what the fluid type was.
         * 
         * @param scalar The scalar to multiply by. */
        void multiplyAmount(double scalar);

        /** @param fluid The new fluid type. This will carry over the amount of fluid to the new type. If the fluid type
         *            is null, then it clears the amount held. */
        void setFluid(Fluid fluid);

        /** @param nbt The new NBT object
         * @throws IllegalArgumentException If {@link #getFluid()} returns null. (We cannot initialise a fluidstack with
         *             a null fluid as that will lead to all sorts of bugs- call {@link #setFluid(Fluid)} with a non
         *             null before if the {@link #getFluid()} returns null) */
        void setNBT(NBTTagCompound nbt) throws IllegalArgumentException;
    }

    public interface IPipeContentsEditablePower extends IPipeContentsEditable, IPipeContentsPower {
        void setPower(double mj);
    }
}
