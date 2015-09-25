/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.internal.pipes;

import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.Maps;

import org.apache.commons.lang3.mutable.MutableDouble;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import buildcraft.api.core.BCLog;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjConnection;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.mj.IMjInternalStorage;
import buildcraft.api.mj.reference.DefaultMjInternalStorage;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.DefaultProps;
import buildcraft.transport.BuildCraftTransport;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.network.PacketPowerUpdate;
import buildcraft.transport.pipes.PipeBehaviourWood;

public final class PipeTransportPower extends PipeTransport implements IDebuggable, IMjExternalStorage {
    public static final byte POWER_STAGES = 32;

    public static final double MAX_POWER = 1024;
    public static final double ACTIVATION = 0.1;
    public static final long LOSS_DELAY = 10;
    public static final double LOSS_RATE = 8;
    /** Should be half of {@link #MAX_POWER} */
    public static final double MAX_TRANSFER = 512;
    public static final double MIN_TRANSFER = 1;

    protected EnumMap<EnumFacing, DefaultMjInternalStorage> pipePartMap = Maps.newEnumMap(EnumFacing.class);
    /** This is used to move power around */
    protected EnumMap<EnumFacing, MutableDouble> powerLastTickMap = Maps.newEnumMap(EnumFacing.class);
    /** This is cached inside the */
    protected EnumMap<EnumFacing, MutableDouble> powerThisTickMap = Maps.newEnumMap(EnumFacing.class);

    /** Used by the client for displaying power */
    public byte[] displayPower = new byte[6];
    /** Used by the client for displaying power flow */
    public byte[] displayFlow = new byte[6];

    /** Used at the client to show flow properly */
    public double[] clientDisplayFlow = new double[6];

    public Vec3 clientDisplayFlowCentre = Utils.VEC_ZERO;

    private SafeTimeTracker tracker = new SafeTimeTracker(2);

    public PipeTransportPower() {}

    @Override
    public EnumPipeType getPipeType() {
        return EnumPipeType.POWER;
    }

    @Override
    public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
        if (tile instanceof IPipeTile) {
            Pipe pipe2 = (Pipe) ((IPipeTile) tile).getPipe();
            if (BlockGenericPipe.isValid(pipe2) && !(pipe2.transport instanceof PipeTransportPower)) {
                return false;
            }
            return true;
        }

        if (tile instanceof IMjHandler) {
            if (container.pipe.behaviour instanceof PipeBehaviourWood) {
                IMjExternalStorage storage = ((IMjHandler) tile).getMjStorage();
                return storage.getDeviceType(side.getOpposite()).givesPowerTo(EnumMjDevice.TRANSPORT);
            }
            IMjExternalStorage storage = ((IMjHandler) tile).getMjStorage();
            return storage.getDeviceType(side.getOpposite()).acceptsPowerFrom(EnumMjDevice.TRANSPORT);
        }
        return false;
    }

    public boolean isPowerSource(TileEntity tile, EnumFacing side) {
        if (!(tile instanceof IMjHandler)) {
            return false;
        }
        IMjExternalStorage storage = ((IMjHandler) tile).getMjStorage();
        if (storage instanceof IMjConnection) {
            IMjConnection connect = (IMjConnection) storage;
            if (!connect.canConnectPower(side.getOpposite(), container.getMjStorage())) {
                return false;
            }
        }
        return storage.getDeviceType(null) == EnumMjDevice.ENGINE && storage.getPowerType(null) == EnumMjPower.NORMAL;
    }

    /** Client-side handler for receiving power updates from the server;
     *
     * @param packetPower */
    public void handlePowerPacket(PacketPowerUpdate packetPower) {
        displayPower = packetPower.power;
        displayFlow = packetPower.flow;
    }

    @Override
    public void updateEntity() {
        if (tracker.markTimeIfDelay(getWorld())) {
            PacketPowerUpdate packet = new PacketPowerUpdate(container.getPos());
            packet.flow = displayFlow;
            packet.power = displayPower;
            BuildCraftTransport.instance.sendToPlayers(packet, container.getWorld(), container.getPos(), DefaultProps.PIPE_CONTENTS_RENDER_DIST);
        }

        // Change this tick to last tick
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        left.add("");
        left.add("PipeTransportPower");
        for (EnumFacing face : EnumFacing.VALUES) {
            if (!container.isPipeConnected(face)) {
                continue;
            }
            int ord = face.ordinal();
            left.add(" - " + face.getName2() + " " + displayPower[ord] + " MJ");
            byte flow = displayFlow[ord];
            left.add("   - flowing " + (flow < 0 ? "in by " + -flow : (flow > 0 ? "out by " + flow : "nowhere")));
            left.add("   - shown flow = " + clientDisplayFlow[ord]);
        }
    }

    @Override
    public EnumMjDevice getDeviceType(EnumFacing side) {
        return EnumMjDevice.TRANSPORT;
    }

    @Override
    public EnumMjPower getPowerType(EnumFacing side) {
        return EnumMjPower.NORMAL;
    }

    @Override
    public double extractPower(World world, EnumFacing flowDirection, IMjExternalStorage to, double minMj, double maxMj, boolean simulate) {
        return 0;// You cannot extract power (directly) from pipes. Neither can pipes.
    }

    @Override
    public double insertPower(World world, EnumFacing flowDirection, IMjExternalStorage from, double mj, boolean simulate) {
        if (!(from instanceof PipeTransportPower)) {
            BCLog.logger.info(from.getClass());
            return mj;// You cannot insert power (directly) to pipes -wooden pipes implement this separately for
                      // redstone engines
        }
        EnumFacing pipePart = flowDirection.getOpposite();
        DefaultMjInternalStorage storage = pipePartMap.get(pipePart);
        double excess = storage.insertPower(world, mj, simulate);
        double actual = mj - excess;
        powerThisTickMap.get(pipePart).add(actual);
        return excess;
    }

    @Override
    public double getSuction(World world, EnumFacing flowDirection) {
        if (flowDirection == null) {
            // Get the suction of all the pipe parts
            double suction = 0;
            for (EnumFacing face : EnumFacing.values()) {
                suction += getSuction(world, face);
            }
            return suction / 6d;
        }
        IMjInternalStorage storage = pipePartMap.get(flowDirection.getOpposite());
        return storage.getSuction();
    }

    @Override
    public void setInternalStorage(IMjInternalStorage storage) {
        // NO-OP (Its handled seperately)
    }

    @Override
    public double currentPower(EnumFacing side) {
        if (side == null) {
            return 0;
        }
        return pipePartMap.get(side).currentPower();
    }

    @Override
    public double maxPower(EnumFacing side) {
        if (side == null) {
            return MAX_POWER;
        }
        return pipePartMap.get(side).maxPower();
    }
}
