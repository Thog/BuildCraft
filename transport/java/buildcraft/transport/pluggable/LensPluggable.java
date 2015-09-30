package buildcraft.transport.pluggable;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.event.IPipeContentsEditable;
import buildcraft.api.transport.event.IPipeContentsEditable.IPipeContentsEditableItem;
import buildcraft.api.transport.event.IPipeEventMovementEnter;
import buildcraft.api.transport.event.IPipeEventMovementExit;
import buildcraft.api.transport.pluggable.IPluggableStaticRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.utils.MatrixTranformations;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.render.LensPluggableRenderer;

import io.netty.buffer.ByteBuf;

public class LensPluggable extends PipePluggable {
    public EnumDyeColor color;
    public boolean isFilter;
    protected IPipeTile container;
    private EnumFacing side;

    public LensPluggable() {

    }

    public LensPluggable(ItemStack stack) {
        color = stack.getItemDamage() & 15;
        isFilter = stack.getItemDamage() >= 16;
    }

    @Override
    public void validate(IPipeTile pipe, EnumFacing direction) {
        this.container = pipe;
        this.side = direction;
    }

    @Override
    public void invalidate() {
        this.container = null;
        this.side = null;
    }

    @Override
    public ItemStack[] getDropItems(IPipeTile pipe) {
        return new ItemStack[] { new ItemStack(BuildCraftTransport.lensItem, 1, color | (isFilter ? 16 : 0)) };
    }

    @Override
    public boolean isBlocking(IPipeTile pipe, EnumFacing direction) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(EnumFacing side) {
        float[][] bounds = new float[3][2];
        // X START - END
        bounds[0][0] = 0.25F - 0.0625F;
        bounds[0][1] = 0.75F + 0.0625F;
        // Y START - END
        bounds[1][0] = 0.000F;
        bounds[1][1] = 0.125F;
        // Z START - END
        bounds[2][0] = 0.25F - 0.0625F;
        bounds[2][1] = 0.75F + 0.0625F;

        MatrixTranformations.transform(bounds, side);
        return new AxisAlignedBB(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    @Override
    public IPluggableStaticRenderer getStaticRenderer() {
        return LensPluggableRenderer.INSTANCE;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        color = tag.getByte("c");
        isFilter = tag.getBoolean("f");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("c", (byte) color);
        tag.setBoolean("f", isFilter);
    }

    @Override
    public void writeData(ByteBuf data) {
        data.writeByte(color | (isFilter ? 0x20 : 0));
    }

    @Override
    public void readData(ByteBuf data) {
        int flags = data.readUnsignedByte();
        color = flags & 15;
        isFilter = (flags & 0x20) > 0;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable o) {
        LensPluggable other = (LensPluggable) o;
        return other.color != color || other.isFilter != isFilter;
    }

    private void color(IPipeContentsEditable editable) {
        if (editable instanceof IPipeContentsEditableItem) {
            ((IPipeContentsEditableItem) editable).setColor(color);
        }
    }

    @Subscribe
    public void onReachedEnd(IPipeEventMovementExit event) {
        if (!isFilter && event.getDestination() == side) {
            color(event.getContents());
        }
    }

    @Subscribe
    public void onEntered(IPipeEventMovementEnter event) {
        if (!isFilter && event.getOrigin() == side) {
            color(event.getContents());
        }
    }
}
