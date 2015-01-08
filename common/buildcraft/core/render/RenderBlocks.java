/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.core.render;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderBlocks {

	public double renderMinX, renderMaxX, renderMinY, renderMaxY, renderMinZ,
			renderMaxZ;

	public int uvRotateEast, uvRotateWest, uvRotateSouth, uvRotateNorth,
			uvRotateTop, uvRotateBottom;;

	public WorldRenderer worldRenderer;

	public boolean renderFromInside, flipTexture;

	/**
	 * RGB colors by side
	 */
	public float colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft,
			colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft,
			colorRedTopRight, colorGreenTopRight, colorBlueTopRight,
			colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight;

	/**
	 * Brightness by side
	 */
	public int brightnessTopLeft, brightnessBottomLeft, brightnessBottomRight,
			brightnessTopRight;

	public boolean enableAO;
	public boolean renderAllFaces;

	public RenderBlocks() {
		this.renderMaxX = 1.0;
		this.renderMaxY = 1.0;
		this.renderMaxZ = 1.0;
	}

	public RenderBlocks(WorldRenderer renderer) {
		this();
		this.worldRenderer = renderer;
	}

	public void setRenderFromInside(boolean value) {
		this.renderFromInside = value;
	}

	public void setRenderBounds(double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ) {
		this.renderMinX = minX;
		this.renderMaxX = maxX;
		this.renderMinY = minY;
		this.renderMaxY = maxY;
		this.renderMinZ = minZ;
		this.renderMaxZ = maxZ;
	}

	/**
	 * Renders the given texture to the bottom face of the block. Args: block,
	 * x, y, z, texture
	 */
	public void renderFaceYNeg(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxX * 16.0D);
		double d5 = (double) texture.getInterpolatedV(this.renderMinZ * 16.0D);
		double d6 = (double) texture.getInterpolatedV(this.renderMaxZ * 16.0D);

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		double d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateBottom == 2) {
			d3 = (double) texture.getInterpolatedU(this.renderMinZ * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxZ * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateBottom == 1) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMaxX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateBottom == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMinX;
		double d12 = x + this.renderMaxX;
		double d13 = y + this.renderMinY;
		double d14 = z + this.renderMinZ;
		double d15 = z + this.renderMaxZ;

		if (this.renderFromInside) {
			d11 = x + this.renderMaxX;
			d12 = x + this.renderMinX;
		}
		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d12, d13, d14, d7, d9);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
		} else {
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
			worldRenderer.addVertexWithUV(d12, d13, d14, d7, d9);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
		}

	}

	/**
	 * Renders the given texture to the top face of the block. Args: x, y, z,
	 * texture
	 */
	public void renderFaceYPos(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxX * 16.0D);
		double d5 = (double) texture.getInterpolatedV(this.renderMinZ * 16.0D);
		double d6 = (double) texture.getInterpolatedV(this.renderMaxZ * 16.0D);

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		double d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateTop == 1) {
			d3 = (double) texture.getInterpolatedU(this.renderMinZ * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxZ * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateTop == 2) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMaxX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateTop == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMinX;
		double d12 = x + this.renderMaxX;
		double d13 = y + this.renderMaxY;
		double d14 = z + this.renderMinZ;
		double d15 = z + this.renderMaxZ;

		if (this.renderFromInside) {
			d11 = x + this.renderMaxX;
			d12 = x + this.renderMinX;
		}

		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d12, d13, d14, d7, d9);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
		} else {
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.addVertexWithUV(d12, d13, d14, d7, d9);
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
		}

	}

	/**
	 * Renders the given texture to the north (z-negative) face of the block.
	 * Args: x, y, z, texture
	 */
	public void renderFaceZNeg(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxX * 16.0D);

		double d5 = (double) texture
				.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = (double) texture
				.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateEast == 2) {
			d3 = (double) texture.getInterpolatedU(this.renderMinY * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxY * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateEast == 1) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxX * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateEast == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMinX;
		double d12 = x + this.renderMaxX;
		double d13 = y + this.renderMinY;
		double d14 = y + this.renderMaxY;
		double d15 = z + this.renderMinZ;

		if (this.renderFromInside) {
			d11 = x + this.renderMaxX;
			d12 = x + this.renderMinX;
		}
		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d11, d14, d15, d7, d9);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d12, d14, d15, d3, d5);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d12, d13, d15, d8, d10);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d11, d13, d15, d4, d6);
		} else {
			worldRenderer.addVertexWithUV(d11, d14, d15, d7, d9);
			worldRenderer.addVertexWithUV(d12, d14, d15, d3, d5);
			worldRenderer.addVertexWithUV(d12, d13, d15, d8, d10);
			worldRenderer.addVertexWithUV(d11, d13, d15, d4, d6);
		}

	}

	/**
	 * Renders the given texture to the south (z-positive) face of the block.
	 * Args: x, y, z, texture
	 */
	public void renderFaceZPos(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxX * 16.0D);
		double d5 = (double) texture
				.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = (double) texture
				.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateWest == 1) {
			d3 = (double) texture.getInterpolatedU(this.renderMinY * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxY * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateWest == 2) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMaxX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateWest == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMinX;
		double d12 = x + this.renderMaxX;
		double d13 = y + this.renderMinY;
		double d14 = y + this.renderMaxY;
		double d15 = z + this.renderMaxZ;

		if (this.renderFromInside) {
			d11 = x + this.renderMaxX;
			d12 = x + this.renderMinX;
		}
		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d11, d14, d15, d3, d5);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d12, d14, d15, d7, d9);
		} else {
			worldRenderer.addVertexWithUV(d11, d14, d15, d3, d5);
			worldRenderer.addVertexWithUV(d11, d13, d15, d8, d10);
			worldRenderer.addVertexWithUV(d12, d13, d15, d4, d6);
			worldRenderer.addVertexWithUV(d12, d14, d15, d7, d9);
		}

	}

	/**
	 * Renders the given texture to the west (x-negative) face of the block.
	 * Args: x, y, z, texture
	 */
	public void renderFaceXNeg(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinZ * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxZ * 16.0D);
		double d5 = (double) texture
				.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = (double) texture
				.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateNorth == 1) {
			d3 = (double) texture.getInterpolatedU(this.renderMinY * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxY * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateNorth == 2) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMinZ * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMaxZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateNorth == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMinX;
		double d12 = y + this.renderMinY;
		double d13 = y + this.renderMaxY;
		double d14 = z + this.renderMinZ;
		double d15 = z + this.renderMaxZ;

		if (this.renderFromInside) {
			d14 = z + this.renderMaxZ;
			d15 = z + this.renderMinZ;
		}

		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d11, d13, d15, d7, d9);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d11, d12, d14, d8, d10);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d11, d12, d15, d4, d6);
		} else {
			worldRenderer.addVertexWithUV(d11, d13, d15, d7, d9);
			worldRenderer.addVertexWithUV(d11, d13, d14, d3, d5);
			worldRenderer.addVertexWithUV(d11, d12, d14, d8, d10);
			worldRenderer.addVertexWithUV(d11, d12, d15, d4, d6);
		}
	}

	/**
	 * Renders the given texture to the east (x-positive) face of the block.
	 * Args: x, y, z, texture
	 */
	public void renderFaceXPos(double x, double y, double z,
			TextureAtlasSprite texture) {

		double d3 = (double) texture.getInterpolatedU(this.renderMinZ * 16.0D);
		double d4 = (double) texture.getInterpolatedU(this.renderMaxZ * 16.0D);
		double d5 = (double) texture
				.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = (double) texture
				.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d3 = (double) texture.getMinU();
			d4 = (double) texture.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = (double) texture.getMinV();
			d6 = (double) texture.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateSouth == 2) {
			d3 = (double) texture.getInterpolatedU(this.renderMinY * 16.0D);
			d5 = (double) texture
					.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
			d4 = (double) texture.getInterpolatedU(this.renderMaxY * 16.0D);
			d6 = (double) texture
					.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateSouth == 1) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxZ * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateSouth == 3) {
			d3 = (double) texture
					.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d4 = (double) texture
					.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = (double) texture.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = (double) texture.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = x + this.renderMaxX;
		double d12 = y + this.renderMinY;
		double d13 = y + this.renderMaxY;
		double d14 = z + this.renderMinZ;
		double d15 = z + this.renderMaxZ;

		if (this.renderFromInside) {
			d14 = z + this.renderMaxZ;
			d15 = z + this.renderMinZ;
		}

		if (this.enableAO) {
			worldRenderer.setColorOpaque_F(this.colorRedTopLeft,
					this.colorGreenTopLeft, this.colorBlueTopLeft);
			worldRenderer.setBrightness(this.brightnessTopLeft);
			worldRenderer.addVertexWithUV(d11, d12, d15, d8, d10);
			worldRenderer.setColorOpaque_F(this.colorRedBottomLeft,
					this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			worldRenderer.setBrightness(this.brightnessBottomLeft);
			worldRenderer.addVertexWithUV(d11, d12, d14, d4, d6);
			worldRenderer.setColorOpaque_F(this.colorRedBottomRight,
					this.colorGreenBottomRight, this.colorBlueBottomRight);
			worldRenderer.setBrightness(this.brightnessBottomRight);
			worldRenderer.addVertexWithUV(d11, d13, d14, d7, d9);
			worldRenderer.setColorOpaque_F(this.colorRedTopRight,
					this.colorGreenTopRight, this.colorBlueTopRight);
			worldRenderer.setBrightness(this.brightnessTopRight);
			worldRenderer.addVertexWithUV(d11, d13, d15, d3, d5);
		} else {
			worldRenderer.addVertexWithUV(d11, d12, d15, d8, d10);
			worldRenderer.addVertexWithUV(d11, d12, d14, d4, d6);
			worldRenderer.addVertexWithUV(d11, d13, d14, d7, d9);
			worldRenderer.addVertexWithUV(d11, d13, d15, d3, d5);
		}
	}

}
