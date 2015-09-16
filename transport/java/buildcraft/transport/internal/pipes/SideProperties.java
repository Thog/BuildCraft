package buildcraft.transport.internal.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.common.util.Constants;

import buildcraft.api.core.BCLog;
import buildcraft.api.transport.PipeManager;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.FacadePluggable;
import buildcraft.transport.item.ItemFacade.FacadeState;
import buildcraft.transport.pluggable.PlugPluggable;

public class SideProperties {
    PipePluggable[] pluggables = new PipePluggable[EnumFacing.VALUES.length];

    public void writeToNBT(NBTTagCompound nbt) {
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            PipePluggable pluggable = pluggables[i];
            final String key = "pluggable[" + i + "]";
            if (pluggable == null) {
                nbt.removeTag(key);
            } else {
                NBTTagCompound pluggableData = new NBTTagCompound();
                pluggableData.setString("pluggableName", PipeManager.getPluggableName(pluggable.getClass()));
                pluggable.writeToNBT(pluggableData);
                nbt.setTag(key, pluggableData);
            }
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            final String key = "pluggable[" + i + "]";
            if (!nbt.hasKey(key)) {
                continue;
            }
            try {
                NBTTagCompound pluggableData = nbt.getCompoundTag(key);
                Class<?> pluggableClass = null;
                // Migration support for 6.1.x/6.2.x // No Longer Required
                // if (pluggableData.hasKey("pluggableClass")) {
                // String c = pluggableData.getString("pluggableClass");
                // if ("buildcraft.transport.gates.ItemGate$GatePluggable".equals(c)) {
                // pluggableClass = GatePluggable.class;
                // } else if ("buildcraft.transport.ItemFacade$FacadePluggable".equals(c)) {
                // pluggableClass = FacadePluggable.class;
                // } else if ("buildcraft.transport.ItemPlug$PlugPluggable".equals(c)) {
                // pluggableClass = PlugPluggable.class;
                // } else if ("buildcraft.transport.gates.ItemRobotStation$RobotStationPluggable".equals(c)
                // || "buildcraft.transport.ItemRobotStation$RobotStationPluggable".equals(c)) {
                // pluggableClass = PipeManager.getPluggableByName("robotStation");
                // }
                // } else {
                pluggableClass = PipeManager.getPluggableByName(pluggableData.getString("pluggableName"));
                // }
                if (!PipePluggable.class.isAssignableFrom(pluggableClass)) {
                    BCLog.logger.warn("Wrong pluggable class: " + pluggableClass);
                    continue;
                }
                PipePluggable pluggable = (PipePluggable) pluggableClass.newInstance();
                pluggable.readFromNBT(pluggableData);
                pluggables[i] = pluggable;
            } catch (Exception e) {
                BCLog.logger.warn("Failed to load side state");
                e.printStackTrace();
            }
        }

        // Migration code
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            PipePluggable pluggable = null;
            if (nbt.hasKey("facadeState[" + i + "]")) {
                pluggable = new FacadePluggable(FacadeState.readArray(nbt.getTagList("facadeState[" + i + "]", Constants.NBT.TAG_COMPOUND)));
            } else {
                // Migration support for 5.0.x and 6.0.x // no longer required
                // if (nbt.hasKey("facadeBlocks[" + i + "]")) {
                // // 5.0.x
                // Block block = (Block) Block.blockRegistry.getObjectById(nbt.getInteger("facadeBlocks[" + i +
                // "]"));
                // int blockId = nbt.getInteger("facadeBlocks[" + i + "]");
                //
                // if (blockId != 0) {
                // int metadata = nbt.getInteger("facadeMeta[" + i + "]");
                // pluggable = new FacadePluggable(new FacadeState[] { FacadeState.create(state) });
                // }
                // } else if (nbt.hasKey("facadeBlocksStr[" + i + "][0]")) {
                // // 6.0.x
                // FacadeState mainState = FacadeState.create((Block)
                // Block.blockRegistry.getObject(nbt.getString("facadeBlocksStr[" + i
                // + "][0]")), nbt.getInteger("facadeMeta[" + i + "][0]"));
                // if (nbt.hasKey("facadeBlocksStr[" + i + "][1]")) {
                // FacadeState phasedState = FacadeState.create((Block)
                // Block.blockRegistry.getObject(nbt.getString("facadeBlocksStr[" + i
                // + "][1]")), nbt.getInteger("facadeMeta[" + i + "][1]"),
                // PipeWire.fromOrdinal(nbt.getInteger("facadeWires[" + i
                // + "]")));
                // pluggable = new FacadePluggable(new FacadeState[] { mainState, phasedState });
                // } else {
                // pluggable = new FacadePluggable(new FacadeState[] { mainState });
                // }
                // }
            }

            if (nbt.getBoolean("plug[" + i + "]")) {
                pluggable = new PlugPluggable();
            }

            if (pluggable != null) {
                pluggables[i] = pluggable;
            }
        }
    }

    public void rotateLeft() {
        PipePluggable[] newPluggables = new PipePluggable[EnumFacing.VALUES.length];
        for (EnumFacing dir : EnumFacing.VALUES) {
            EnumFacing rotated = dir.getAxis() == Axis.Y ? dir : dir.rotateY();
            newPluggables[rotated.ordinal()] = pluggables[dir.ordinal()];
        }
        pluggables = newPluggables;
    }

    public boolean dropItem(TileGenericPipe pipe, EnumFacing direction, EntityPlayer player) {
        boolean result = false;
        PipePluggable pluggable = pluggables[direction.ordinal()];
        if (pluggable != null) {
            pluggable.onDetachedPipe(pipe, direction);
            if (!pipe.getWorld().isRemote) {
                ItemStack[] stacks = pluggable.getDropItems(pipe);
                if (stacks != null) {
                    for (ItemStack stack : stacks) {
                        Utils.dropTryIntoPlayerInventory(pipe.getWorld(), pipe.getPos(), stack, player);
                    }
                }
            }
            result = true;
        }
        return result;
    }

    public void invalidate() {
        for (PipePluggable p : pluggables) {
            if (p != null) {
                p.invalidate();
            }
        }
    }

    public void validate(TileGenericPipe pipe) {
        for (EnumFacing d : EnumFacing.VALUES) {
            PipePluggable p = pluggables[d.ordinal()];

            if (p != null) {
                p.validate(pipe, d);
            }
        }
    }
}
