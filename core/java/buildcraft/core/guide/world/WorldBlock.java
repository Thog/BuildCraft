package buildcraft.core.guide.world;

import java.util.Map;

import net.minecraft.util.BlockPos;

public class WorldBlock {
    public final String id;
    public final BlockPos pos;
    public final String state;
    public final Map<String, String> extraPoperties;

    public WorldBlock(String id, BlockPos pos, String state, Map<String, String> extraPoperties) {
        this.id = id;
        this.pos = pos;
        this.state = state;
        this.extraPoperties = extraPoperties;
    }
}
