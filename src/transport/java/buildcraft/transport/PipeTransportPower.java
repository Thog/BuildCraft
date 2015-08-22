/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.EnumMjDevice;
import buildcraft.api.mj.EnumMjPower;
import buildcraft.api.mj.IMjConnection;
import buildcraft.api.mj.IMjExternalStorage;
import buildcraft.api.mj.IMjHandler;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.DefaultProps;
import buildcraft.transport.block.BlockGenericPipe;
import buildcraft.transport.network.PacketPowerUpdate;
import buildcraft.transport.pipes.PipePowerWood;

public class PipeTransportPower extends PipeTransport implements IDebuggable {
    public static final byte POWER_STAGES = 32;

    /** Used by the client for displaying power */
    public byte[] displayPower = new byte[6], preDisplayPower = new byte[6];
    /** Used by the client for displaying power flow */
    public byte[] displayFlow = new byte[6], preDisplayFlow = new byte[6];

    public long lastRecievedTime = 0, preRecievedTime = -1;

    private SafeTimeTracker tracker = new SafeTimeTracker(2);

    public PipeTransportPower() {}

    @Override
    public IPipeTile.PipeType getPipeType() {
        return IPipeTile.PipeType.POWER;
    }

    @Override
    public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
        if (tile instanceof IPipeTile) {
            Pipe<?> pipe2 = (Pipe<?>) ((IPipeTile) tile).getPipe();
            if (BlockGenericPipe.isValid(pipe2) && !(pipe2.transport instanceof PipeTransportPower)) {
                return false;
            }
            return true;
        }

        if (tile instanceof IMjHandler) {
            if (container.pipe instanceof PipePowerWood) {
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
        preDisplayPower = displayPower;
        preDisplayFlow = displayFlow;
        preRecievedTime = lastRecievedTime;

        displayPower = packetPower.power;
        displayFlow = packetPower.flow;
        lastRecievedTime = System.currentTimeMillis();
    }

    public double[] interpolatePower() {
        return interpolate(preDisplayPower, displayPower);
    }

    public double[] interpolateFlow() {
        return interpolate(preDisplayFlow, displayFlow);
    }

    private double[] interpolate(byte[] lastArray, byte[] nowArray) {
        long now = System.currentTimeMillis();
        long diffLast = lastRecievedTime - preRecievedTime;
        long diffNow = now - lastRecievedTime;
        double interpDiff = (double) diffNow / (double) diffLast;
        double[] array = new double[6];
        for (int i = 0; i < 6; i++) {
            array[i] = (1 - interpDiff) * lastArray[i] + interpDiff * nowArray[i];
        }
        return array;
    }

    @Override
    public void updateEntity() {
        if (tracker.markTimeIfDelay(getWorld())) {
            PacketPowerUpdate packet = new PacketPowerUpdate(container.getPos());
            packet.flow = displayFlow;
            packet.power = displayPower;
            BuildCraftTransport.instance.sendToPlayers(packet, container.getWorld(), container.getPos(), DefaultProps.PIPE_CONTENTS_RENDER_DIST);
        }
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
            left.add(" -   flowing " + (flow < 0 ? "in by " + -flow : (flow > 0 ? "out by " + flow : "nowhere")));
        }
    }
}
