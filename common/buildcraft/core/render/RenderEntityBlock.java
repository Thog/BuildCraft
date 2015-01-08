/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.core.render;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import buildcraft.core.EntityBlock;

public final class RenderEntityBlock extends Render {

	public static RenderEntityBlock INSTANCE = new RenderEntityBlock();
	protected RenderBlocks renderBlocks;

	private RenderEntityBlock() {
		super(Minecraft.getMinecraft().getRenderManager());
		renderBlocks = new RenderBlocks();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static class RenderInfo {

		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;
		public Block baseBlock = Blocks.sand;
		public TextureAtlasSprite texture = null;
		public TextureAtlasSprite[] textureArray = null;
		public boolean[] renderSide = new boolean[6];
		public float light = -1f;
		public int brightness = -1;
		public IBlockState blockState;

		public RenderInfo() {
			setRenderAllSides();
		}

		public RenderInfo(Block template, TextureAtlasSprite[] texture) {
			this();
			this.baseBlock = template;
			this.textureArray = texture;
		}

		public RenderInfo(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
			this();
			setBounds(minX, minY, minZ, maxX, maxY, maxZ);
		}

		public float getBlockBrightness(IBlockAccess iblockaccess, BlockPos pos) {
			return baseBlock.getMixedBrightnessForBlock(iblockaccess, pos);
		}

		public final void setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		public final void setRenderSingleSide(int side) {
			Arrays.fill(renderSide, false);
			renderSide[side] = true;
		}

		public final void setRenderAllSides() {
			Arrays.fill(renderSide, true);
		}

		public void rotate() {
			double temp = minX;
			minX = minZ;
			minZ = temp;

			temp = maxX;
			maxX = maxZ;
			maxZ = temp;
		}

		public void reverseX() {
			double temp = minX;
			minX = 1 - maxX;
			maxX = 1 - temp;
		}

		public void reverseZ() {
			double temp = minZ;
			minZ = 1 - maxZ;
			maxZ = 1 - temp;
		}

		public TextureAtlasSprite getBlockTextureFromSide(int i) {
			if (texture != null) {
				return texture;
			}

			int index = i;

			if (textureArray == null || textureArray.length == 0) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(baseBlock.getUnlocalizedName());
			} else {
				if (index >= textureArray.length) {
					index = 0;
				}

				return textureArray[index];
			}
		}
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {
		doRenderBlock((EntityBlock) entity, x, y, z);
	}

	public void doRenderBlock(EntityBlock entity, double x, double y, double z) {
		if (entity.isDead) {
			return;
		}

		shadowSize = entity.shadowSize;
		RenderInfo util = new RenderInfo();
		util.texture = entity.texture;
		bindTexture(TextureMap.locationBlocksTexture);

		for (int iBase = 0; iBase < entity.iSize; ++iBase) {
			for (int jBase = 0; jBase < entity.jSize; ++jBase) {
				for (int kBase = 0; kBase < entity.kSize; ++kBase) {

					util.minX = 0;
					util.minY = 0;
					util.minZ = 0;

					double remainX = entity.iSize - iBase;
					double remainY = entity.jSize - jBase;
					double remainZ = entity.kSize - kBase;

					util.maxX = remainX > 1.0 ? 1.0 : remainX;
					util.maxY = remainY > 1.0 ? 1.0 : remainY;
					util.maxZ = remainZ > 1.0 ? 1.0 : remainZ;

					GL11.glPushMatrix();
					GL11.glTranslatef((float) x, (float) y, (float) z);
					GL11.glRotatef(entity.rotationX, 1, 0, 0);
					GL11.glRotatef(entity.rotationY, 0, 1, 0);
					GL11.glRotatef(entity.rotationZ, 0, 0, 1);
					GL11.glTranslatef(iBase, jBase, kBase);

					renderBlock(util);
					GL11.glPopMatrix();

				}
			}
		}
	}

	public void renderBlock(RenderInfo info) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderBlocks.worldRenderer = renderer;
		if(info.blockState != null){
			BlockRendererDispatcher renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();
			BlockPos pos = BlockPos.ORIGIN;
			renderer.startDrawingQuads();
			renderer.setVertexFormat(DefaultVertexFormats.BLOCK);
			renderer.setTranslation(0, 0, 0);
			GlStateManager.scale(info.maxX - info.minX, info.maxY - info.minY, info.maxZ - info.minZ);
			renderBlocks.renderBlock(info.blockState, pos, Minecraft.getMinecraft().theWorld, renderer);
			tessellator.draw();
		}
		else{

			renderer.startDrawingQuads();
			
			renderBlocks.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);
			
			if (info.renderSide[0]) {
				renderer.setNormal(0, -1, 0);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceYNeg(0, 0, 0, info.getBlockTextureFromSide(0));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceYNeg(0, 0, 0, info.getBlockTextureFromSide(0));
			}
			if (info.renderSide[1]) {
				renderer.setNormal(0, 1, 0);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceYPos(0, 0, 0, info.getBlockTextureFromSide(1));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceYPos(0, 0, 0, info.getBlockTextureFromSide(1));
			}
			if (info.renderSide[2]) {
				renderer.setNormal(0, 0, -1);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceZNeg(0, 0, 0, info.getBlockTextureFromSide(2));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceZNeg(0, 0, 0, info.getBlockTextureFromSide(2));
			}
			if (info.renderSide[3]) {
				renderer.setNormal(0, 0, 1);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceZPos(0, 0, 0, info.getBlockTextureFromSide(3));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceZPos(0, 0, 0, info.getBlockTextureFromSide(3));
			}
			if (info.renderSide[4]) {
				renderer.setNormal(-1, 0, 0);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceXNeg(0, 0, 0, info.getBlockTextureFromSide(4));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceXNeg(0, 0, 0, info.getBlockTextureFromSide(4));
			}
			if (info.renderSide[5]) {
				renderer.setNormal(1, 0, 0);
				renderBlocks.setRenderFromInside(true);
				renderBlocks.renderFaceXPos(0, 0, 0, info.getBlockTextureFromSide(5));
				renderBlocks.setRenderFromInside(false);
				renderBlocks.renderFaceXPos(0, 0, 0, info.getBlockTextureFromSide(5));
			}
			tessellator.draw();
			
		}
	}
}