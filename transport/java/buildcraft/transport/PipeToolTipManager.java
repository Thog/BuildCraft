/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.transport.PipeDefinition;
import buildcraft.core.BuildCraftCore;
import buildcraft.core.lib.utils.StringUtils;

@SideOnly(Side.CLIENT)
public final class PipeToolTipManager {

    private static final Map<PipeDefinition, String> toolTips = Maps.newHashMap();

    static {
        if (!BuildCraftCore.hideFluidNumbers) {
            // for (Map.Entry<Class<? extends Pipe>, Integer> pipe : PipeTransportFluids.fluidCapacities.entrySet()) {
            // PipeToolTipManager.addToolTip(pipe.getKey(), String.format("%d mB/t", pipe.getValue()));
            // }
        }
    }

    /** Deactivate constructor */
    private PipeToolTipManager() {}

    private static void addTipToList(String tipTag, List<String> tips) {
        if (StringUtils.canLocalize(tipTag)) {
            String localized = StringUtils.localize(tipTag);
            if (localized != null) {
                List<String> lines = StringUtils.newLineSplitter.splitToList(localized);
                tips.addAll(lines);
            }
        }
    }

    // public static void addToolTip(Class<? extends Pipe<?>> pipe, String toolTip) {
    // toolTips.put(pipe, toolTip);
    // }

    public static List<String> getToolTip(PipeDefinition pipe, boolean advanced) {
        if (pipe == null) {
            return Collections.emptyList();
        }
        List<String> tips = Lists.newArrayList();
        addTipToList("tip." + pipe.globalUniqueTag, tips);

        String tip = toolTips.get(pipe);
        if (tip != null) {
            tips.add(tip);
        }

        if (GuiScreen.isShiftKeyDown()) {
            addTipToList("tip.shift." + pipe.globalUniqueTag, tips);
        }
        return tips;
    }
}
