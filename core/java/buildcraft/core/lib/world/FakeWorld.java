package buildcraft.core.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

import buildcraft.api.blueprints.SchematicBlockBase;
import buildcraft.core.blueprints.Blueprint;
import buildcraft.core.blueprints.BptBuilderBlueprint;
import buildcraft.core.blueprints.Template;

public class FakeWorld extends World {
    private final FakeChunkProvider chunkProvider = new FakeChunkProvider(this);

    private FakeWorld() {
        super(new SaveHandlerMP(), new WorldInfo(new NBTTagCompound()), new FakeWorldProvider(), Minecraft.getMinecraft().mcProfiler, false);
    }

    public FakeWorld(Blueprint blueprint) {
        this();
        BptBuilderBlueprint bpt = new BptBuilderBlueprint(blueprint, this, new BlockPos(0, 1, 0));
        bpt.deploy();
    }

    public FakeWorld(Template template, IBlockState filledBlock) {
        this();
        for (int x = 0; x < template.sizeX; x++) {
            for (int y = 0; y < template.sizeY; y++) {
                for (int z = 0; z < template.sizeZ; z++) {
                    SchematicBlockBase block = template.contents[x][y][z];
                    if (block != null) {
                        setBlockState(new BlockPos(x, y + 1, z), filledBlock);
                    }
                }
            }
        }

    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return chunkProvider;
    }

    @Override
    protected int getRenderDistanceChunks() {
        return 10;
    }
}
