/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport.statements;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.gates.IGate;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.StatementParameterItemStack;
import buildcraft.api.transport.EnumPipeType;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeAPI;
import buildcraft.api.transport.event.IPipeContents;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;
import buildcraft.core.lib.inventory.StackHelper;
import buildcraft.core.lib.utils.StringUtils;
import buildcraft.core.statements.BCStatement;

public class TriggerPipeContents extends BCStatement implements ITriggerInternal {

    public enum PipeContents {
        empty,
        containsItems,
        containsFluids,
        containsEnergy,
        tooMuchEnergy;
        public ITriggerInternal trigger;
    }

    private PipeContents kind;

    public TriggerPipeContents(PipeContents kind) {
        super("buildcraft:pipe.contents." + kind.name().toLowerCase(Locale.ENGLISH), "buildcraft.pipe.contents." + kind.name());
        this.location = new ResourceLocation("buildcrafttransport:triggers/trigger_pipecontents_" + kind.name().toLowerCase(Locale.ENGLISH));
        this.kind = kind;
        kind.trigger = this;
    }

    @Override
    public int maxParameters() {
        switch (kind) {
            case containsItems:
            case containsFluids:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String getDescription() {
        return StringUtils.localize("gate.trigger.pipe." + kind.name());
    }

    @Override
    public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
        if (!(container instanceof IGate)) {
            return false;
        }

        IPipe pipe = (IPipe) ((IGate) container).getPipe();
        IStatementParameter parameter = parameters[0];

        if (pipe.getBehaviour().definition.type == EnumPipeType.ITEM) {
            if (kind == PipeContents.empty) {
                return pipe.getProperty(PipeAPI.STACK_COUNT) == 0;
            } else if (kind == PipeContents.containsItems) {
                if (parameter != null && parameter.getItemStack() != null) {
                    for (IPipeContents contents : pipe.getProperty(PipeAPI.CONTENTS)) {
                        if (contents instanceof IPipeContentsItem) {
                            IPipeContentsItem items = (IPipeContentsItem) contents;
                            if (StackHelper.isMatchingItemOrList(parameter.getItemStack(), items.getStack())) {
                                return true;
                            }
                        }
                    }
                } else {
                    return pipe.getProperty(PipeAPI.STACK_COUNT) > 0;
                }
            }
        } else if (pipe.getBehaviour().definition.type == EnumPipeType.FLUID) {
            int amount = pipe.getProperty(PipeAPI.FLUID_AMOUNT);
            Fluid fluid = pipe.getProperty(PipeAPI.FLUID_TYPE);

            Fluid searchedFluid = null;

            if (parameter != null && parameter.getItemStack() != null) {
                FluidStack itemFluid = FluidContainerRegistry.getFluidForFilledItem(parameter.getItemStack());
                if (itemFluid != null) {
                    searchedFluid = itemFluid.getFluid();
                }
            }

            if (kind == PipeContents.empty) {
                return amount == 0;
            } else {
                if (amount == 0) {
                    return false;
                }
                if (searchedFluid != null) {
                    return fluid == searchedFluid;
                }
                return true;
            }
        } else if (pipe.getBehaviour().definition.type == EnumPipeType.POWER) {
            double power = pipe.getProperty(PipeAPI.POWER);
            int percentFull = pipe.getProperty(PipeAPI.PERCENT_FULL);
            switch (kind) {
                case empty:
                    return power < 1e-4;
                case containsEnergy:
                    return power > 1e-4;
                default:
                case tooMuchEnergy: {
                    return percentFull == 100;
                }
            }
        }

        return false;
    }

    @Override
    public IStatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }
}
