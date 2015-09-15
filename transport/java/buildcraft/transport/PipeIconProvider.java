/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.transport;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.core.IIconProvider;
import buildcraft.transport.pipes.PipeInfo;

public class PipeIconProvider implements IIconProvider {

    public static final PipeIconProvider instance = new PipeIconProvider();

    private static final BiMap<PipeInfo, Integer> pipeIndexes = HashBiMap.create();
    private static int currentIndex = 0;

    @SideOnly(Side.CLIENT)
    private static final Map<Integer, TextureAtlasSprite> sprites = Maps.newHashMap();

    public static void registerPipe(PipeInfo info) {
        pipeIndexes.put(info, currentIndex++);
    }

    public static int getIndex(PipeInfo info) {
        Integer integer = pipeIndexes.get(info);
        if (integer == null) {
            return -1;
        }
        return integer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon(int pipeIconIndex) {
        return sprites.get(pipeIconIndex);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(TextureMap iconRegister) {
        sprites.clear();
        for (Entry<PipeInfo, Integer> entry : pipeIndexes.entrySet()) {
            String textureLocation = entry.getKey().getSpriteLocation();
            int index = entry.getValue();
            TextureAtlasSprite sprite = iconRegister.registerSprite(new ResourceLocation(textureLocation));
            sprites.put(index, sprite);
        }
    }
}
