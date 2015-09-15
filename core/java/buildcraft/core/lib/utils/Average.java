/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import buildcraft.api.core.ISerializable;

import io.netty.buffer.ByteBuf;

public class Average implements ISerializable {
    private double[] data;
    private int pos, precise;
    private double averageRaw, tickValue;

    public Average(int precise) {
        this.precise = precise;
        this.data = new double[precise];
        this.pos = 0;
    }

    public double getAverage() {
        return averageRaw / precise;
    }

    public void tick(double value) {
        internalTick(tickValue + value);
        tickValue = 0;
    }

    public void tick() {
        internalTick(tickValue);
        tickValue = 0;
    }

    private void internalTick(double value) {
        pos = ++pos % precise;
        double oldValue = data[pos];
        data[pos] = value;
        if (pos == 0) {
            averageRaw = 0;
            for (double iValue : data) {
                averageRaw += iValue;
            }
        } else {
            averageRaw = averageRaw - oldValue + value;
        }
    }

    public void push(double value) {
        tickValue += value;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasNoTags()) {
            // We're upgrading from something that didn't have this tag- leave settings as they are
            return;
        }
        NBTTagList list = nbt.getTagList("data", NBT.TAG_DOUBLE);
        data = new double[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++) {
            data[i] = list.getDoubleAt(i);
        }
        pos = nbt.getInteger("pos");
        precise = nbt.getInteger("precise");
        averageRaw = nbt.getDouble("averageRaw");
        tickValue = nbt.getDouble("tickValue");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (double value : data) {
            list.appendTag(new NBTTagDouble(value));
        }
        nbt.setTag("data", list);
        nbt.setInteger("pos", pos);
        nbt.setInteger("precise", precise);
        nbt.setDouble("averageRaw", averageRaw);
        nbt.setDouble("tickValue", tickValue);
        return nbt;
    }

    @Override
    public void writeData(ByteBuf stream) {
        stream.writeInt(data.length);
        // for (double d : data) {
        // stream.writeDouble(d);
        // }
        stream.writeDouble(averageRaw);
    }

    @Override
    public void readData(ByteBuf stream) {
        precise = stream.readInt();
        // data = new double[precise];
        // for (int i = 0; i < data.length; i++) {
        // data[i] = stream.readDouble();
        // }
        averageRaw = stream.readDouble();
    }
}
