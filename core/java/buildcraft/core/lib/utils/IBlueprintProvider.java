package buildcraft.core.lib.utils;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.blueprints.IBuilderContext;
import buildcraft.core.blueprints.BptBuilderBase;

public interface IBlueprintProvider {
    class Providers {
        public static final Set<IBlueprintProvider> markers = Sets.newHashSet();
    }

    World getWorld();

    BlockPos getPos();

    boolean needsToBuild();

    IBuilderContext getContext();

    BptBuilderBase getBlueprintBuilder();
}
