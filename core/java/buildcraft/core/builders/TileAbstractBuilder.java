/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.builders;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;

import buildcraft.api.blueprints.ITileBuilder;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.reference.DefaultMjExternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.core.BuildCraftCore;
import buildcraft.core.LaserData;
import buildcraft.core.internal.IBoxProvider;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.network.Packet;
import buildcraft.core.lib.network.command.CommandWriter;
import buildcraft.core.lib.network.command.ICommandReceiver;
import buildcraft.core.lib.network.command.PacketCommand;

import io.netty.buffer.ByteBuf;

public abstract class TileAbstractBuilder extends TileBuildCraft implements ITileBuilder, IInventory, IBoxProvider, IBuildingItemsProvider,
        ICommandReceiver, IMjHandler, IDebuggable {

    public LinkedList<LaserData> pathLasers = new LinkedList<LaserData>();

    public Set<BuildingItem> buildersInAction = Sets.newConcurrentHashSet();

    protected final DefaultMjExternalStorage externalStorage;
    protected final DefaultMjInternalStorage internalStorage;

    protected TileAbstractBuilder(double maxPower, double maxPowerTransfered, double activationPower, long lossDelay, double lossRate) {
        externalStorage = new DefaultMjExternalStorage(EnumMjDevice.MACHINE, EnumMjPower.NORMAL, maxPowerTransfered);
        internalStorage = new DefaultMjInternalStorage(maxPower, activationPower, lossDelay, lossRate);
        externalStorage.setInternalStorage(internalStorage);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (worldObj.isRemote) {
            BuildCraftCore.instance.sendToServer(new PacketCommand(this, "uploadBuildersInAction", null));
        }
    }

    private Packet createLaunchItemPacket(final BuildingItem i) {
        return new PacketCommand(this, "launchItem", new CommandWriter() {
            public void write(ByteBuf data) {
                i.writeData(data);
            }
        });
    }

    @Override
    public void receiveCommand(String command, Side side, Object sender, ByteBuf stream) {
        if (side.isServer() && "uploadBuildersInAction".equals(command)) {
            for (BuildingItem i : buildersInAction) {
                BuildCraftCore.instance.sendToPlayer((EntityPlayer) sender, createLaunchItemPacket(i));
            }
        } else if (side.isClient() && "launchItem".equals(command)) {
            BuildingItem item = new BuildingItem();
            item.readData(stream);
            buildersInAction.add(item);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            internalStorage.tick(getWorld());
        }

        Iterator<BuildingItem> itemIterator = buildersInAction.iterator();
        BuildingItem i;

        while (itemIterator.hasNext()) {
            i = itemIterator.next();
            i.update();

            if (i.isDone()) {
                itemIterator.remove();
            }
        }
    }

    @Override
    public Collection<BuildingItem> getBuilders() {
        return buildersInAction;
    }

    public LinkedList<LaserData> getPathLaser() {
        return pathLasers;
    }

    @Override
    public void addAndLaunchBuildingItem(BuildingItem item) {
        buildersInAction.add(item);
        BuildCraftCore.instance.sendToPlayersNear(createLaunchItemPacket(item), this);
    }

    public final double powerAvailable() {
        return internalStorage.currentPower();
    }

    public final boolean consumePower(double quantity) {
        return internalStorage.extractPower(getWorld(), quantity, quantity, false) == quantity;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("internalStorage", internalStorage.writeToNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        internalStorage.readFromNBT(nbt.getCompoundTag("internalStorage"));
    }

    @Override
    public void readData(ByteBuf stream) {
        super.readData(stream);
        int size = stream.readUnsignedShort();
        pathLasers.clear();
        for (int i = 0; i < size; i++) {
            LaserData ld = new LaserData();
            ld.readData(stream);
            pathLasers.add(ld);
        }
        internalStorage.readData(stream);
    }

    @Override
    public void writeData(ByteBuf stream) {
        super.writeData(stream);
        stream.writeShort(pathLasers.size());
        for (LaserData ld : pathLasers) {
            ld.writeData(stream);
        }
        internalStorage.writeData(stream);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Double.MAX_VALUE;
    }

    public boolean drainBuild(FluidStack fluidStack, boolean realDrain) {
        return false;
    }

    @Override
    public IMjExternalStorage getMjStorage() {
        return externalStorage;
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        left.add("");
        left.add("Internal Storage:");
        left.add("  - power = " + internalStorage.currentPower() + "Mj");
        left.add("  - max = " + internalStorage.maxPower() + "Mj");
        left.add("  - active = " + internalStorage.hasActivated());
        left.add("  - operating = " + internalStorage.isOperating());
    }
}
