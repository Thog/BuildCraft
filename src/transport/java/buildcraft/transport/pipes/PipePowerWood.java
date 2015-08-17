/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.pipes;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeIconProvider;

public class PipePowerWood extends PipePowerBase implements IDebuggable {
    public final boolean[] powerSources = new boolean[6];

    protected int standardIconIndex = PipeIconProvider.TYPE.PipePowerWood_Standard.ordinal();
    protected int solidIconIndex = PipeIconProvider.TYPE.PipeAllWood_Solid.ordinal();

    // private boolean full;
    private int requestedEnergy, sources, lastRequestedEnergy;

    private boolean allowExtraction = false;

    public PipePowerWood(Item item) {
        super(item);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider() {
        return BuildCraftTransport.instance.pipeIconProvider;
    }

    @Override
    public int getIconIndex(EnumFacing direction) {
        if (direction != null && powerSources[direction.ordinal()]) {
            return solidIconIndex;
        } else {
            return standardIconIndex;
        }
    }

    @Override
    public void update() {
        super.update();

        sources = 0;

        for (EnumFacing o : EnumFacing.VALUES) {
            boolean oldPowerSource = powerSources[o.ordinal()];

            if (!container.isPipeConnected(o)) {
                powerSources[o.ordinal()] = false;
            } else {
                TileEntity tile = container.getTile(o);

                if (powerSources[o.ordinal()] = transport.isPowerSource(tile, o)) {
                    sources++;
                }
            }

            if (oldPowerSource != powerSources[o.ordinal()]) {
                container.scheduleRenderUpdate();
            }
        }

        if (container.getWorld().isRemote) {
            // We only do the isRemote check now to get a list
            // of power sources for client-side rendering.
            return;
        }

        /* if (sources <= 0) { battery.useEnergy(0, 50, false); requestedEnergy = 0; return; } if (sources == 0) {
         * return; } if (allowExtraction) { allowExtraction = false; int energyMaxExtract =
         * Math.min(battery.getMaxEnergyExtract(), battery.getMaxEnergyStored() - battery.getEnergyStored());
         * energyMaxExtract /= sources; for (EnumFacing o : EnumFacing.VALUES) { if (!powerSources[o.ordinal()]) {
         * continue; } TileEntity source = container.getNeighborTile(o); if (source instanceof IEnergyProvider) { int
         * energyExtracted = battery.addEnergy(0, ((IEnergyProvider) source).extractEnergy(o.getOpposite(),
         * energyMaxExtract, true), false); ((IEnergyProvider) source).extractEnergy(o.getOpposite(), energyExtracted,
         * true); } else if (source instanceof IEnergyHandler) { int energyExtracted = battery.addEnergy(0,
         * ((IEnergyHandler) source).extractEnergy(o.getOpposite(), energyMaxExtract, true), false); ((IEnergyHandler)
         * source).extractEnergy(o.getOpposite(), energyExtracted, true); } } } int energyToRemove =
         * Math.min(battery.getEnergyStored(), requestedEnergy); energyToRemove /= sources; if
         * (battery.getEnergyStored() > 0) { for (EnumFacing o : EnumFacing.VALUES) { if (!powerSources[o.ordinal()]) {
         * continue; } // PipePowerWood's resistance is 0, so this is fine. battery.setEnergy(battery.getEnergyStored()
         * - (int) transport.receiveEnergy(o, energyToRemove)); } } lastRequestedEnergy = requestedEnergy;
         * requestedEnergy = 0; */
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        // NBTTagCompound batteryNBT = new NBTTagCompound();
        // battery.writeToNBT(batteryNBT);
        // data.setTag("battery", batteryNBT);

        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            data.setBoolean("powerSources[" + i + "]", powerSources[i]);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        // battery.readFromNBT(data.getCompoundTag("battery"));

        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            powerSources[i] = data.getBoolean("powerSources[" + i + "]");
        }
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        if (from.getPowerType(null) == EnumMjPower.REDSTONE) {
            // If the from is a redstone engine than redirect the power to allow it to extract this tick
            allowExtraction = true;
            return 0;
        } else {
            return super.insertPower(world, flowDirection, from, mj, simulate);
        }
    }

    /* @Override public int receiveEnergy(EnumFacing from, int val) { return -1; }
     * @Override public int requestEnergy(EnumFacing from, int amount) { if (container.getTile(from) instanceof
     * IPipeTile) { requestedEnergy += amount; return amount; } else { return 0; } } */

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing face) {
        left.add("");
        left.add("Power Acceptor");
        left.add("- requestedEnergy: " + requestedEnergy);
        left.add("- lastRequestedEnergy: " + lastRequestedEnergy);
    }
}
