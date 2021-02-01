package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.render.RenderTypes;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.AngleHelper;
import mod.grimmauld.schematicprinter.util.ColorHelper;
import mod.grimmauld.schematicprinter.util.VecHelper;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class Outline {
	final protected OutlineParams params = new OutlineParams();
	protected Matrix3f transformNormals;

	public Outline() {
	}

	public abstract void render(MatrixStack var1, SuperRenderTypeBuffer var2);

	public void renderCuboidLine(MatrixStack ms, SuperRenderTypeBuffer buffer, Vec3d start, Vec3d end) {
		Vec3d diff = end.subtract(start);
		float hAngle = AngleHelper.deg(MathHelper.atan2(diff.x, diff.z));
		float hDistance = (float) diff.mul(1.0D, 0.0D, 1.0D).length();
		float vAngle = AngleHelper.deg(MathHelper.atan2(hDistance, diff.y)) - 90.0F;
		ms.push();
		ms.translate(start.x, start.y, start.z);
		ms.rotate(Vector3f.YP.rotationDegrees(hAngle));
		ms.rotate(Vector3f.XP.rotationDegrees(vAngle));
		this.renderAACuboidLine(ms, buffer, Vec3d.ZERO, new Vec3d(0.0D, 0.0D, diff.length()));
		ms.pop();
	}

	public void renderAACuboidLine(MatrixStack ms, SuperRenderTypeBuffer buffer, Vec3d start, Vec3d end) {
		float lineWidth = this.params.getLineWidth();
		if (lineWidth != 0.0F) {
			IVertexBuilder builder = buffer.getBuffer(RenderTypes.getOutlineSolid());
			Vec3d diff = end.subtract(start);
			Vec3d extension;
			if (diff.x + diff.y + diff.z < 0.0D) {
				extension = start;
				start = end;
				end = extension;
				diff = diff.scale(-1.0D);
			}

			extension = diff.normalize().scale(lineWidth / 2.0F);
			Vec3d plane = VecHelper.axisAlingedPlaneOf(diff);
			Direction face = Direction.getFacingFromVector(diff.x, diff.y, diff.z);
			Direction.Axis axis = face.getAxis();
			start = start.subtract(extension);
			end = end.add(extension);
			plane = plane.scale(lineWidth / 2.0F);
			Vec3d a1 = plane.add(start);
			Vec3d b1 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vec3d a2 = plane.add(start);
			Vec3d b2 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vec3d a3 = plane.add(start);
			Vec3d b3 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vec3d a4 = plane.add(start);
			Vec3d b4 = plane.add(end);
			if (this.params.disableNormals) {
				face = Direction.UP;
				this.putQuad(ms, builder, b4, b3, b2, b1, face);
				this.putQuad(ms, builder, a1, a2, a3, a4, face);
				this.putQuad(ms, builder, a1, b1, b2, a2, face);
				this.putQuad(ms, builder, a2, b2, b3, a3, face);
				this.putQuad(ms, builder, a3, b3, b4, a4, face);
			} else {
				this.putQuad(ms, builder, b4, b3, b2, b1, face);
				this.putQuad(ms, builder, a1, a2, a3, a4, face.getOpposite());
				Vec3d vec = a1.subtract(a4);
				face = Direction.getFacingFromVector(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a1, b1, b2, a2, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getFacingFromVector(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a2, b2, b3, a3, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getFacingFromVector(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a3, b3, b4, a4, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getFacingFromVector(vec.x, vec.y, vec.z);
			}
			this.putQuad(ms, builder, a4, b4, b1, a1, face);
		}
	}

	public void putQuad(MatrixStack ms, IVertexBuilder builder, Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, Direction normal) {
		this.putQuadUV(ms, builder, v1, v2, v3, v4, 0.0F, 0.0F, 1.0F, 1.0F, normal);
	}

	public void putQuadUV(MatrixStack ms, IVertexBuilder builder, Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, float minU, float minV, float maxU, float maxV, Direction normal) {
		this.putVertex(ms, builder, v1, minU, minV, normal);
		this.putVertex(ms, builder, v2, maxU, minV, normal);
		this.putVertex(ms, builder, v3, maxU, maxV, normal);
		this.putVertex(ms, builder, v4, minU, maxV, normal);
	}

	protected void putVertex(MatrixStack ms, IVertexBuilder builder, Vec3d pos, float u, float v, Direction normal) {
		int i = 15728880;
		int j = i >> 16 & '\uffff';
		int k = i & '\uffff';
		MatrixStack.Entry peek = ms.getLast();
		Vec3d rgb = this.params.rgb;
		if (this.transformNormals == null) {
			this.transformNormals = peek.getNormal();
		}

		int xOffset = 0;
		int yOffset = 0;
		int zOffset = 0;
		if (normal != null) {
			xOffset = normal.getXOffset();
			yOffset = normal.getYOffset();
			zOffset = normal.getZOffset();
		}

		builder.pos(peek.getMatrix(), (float) pos.x, (float) pos.y, (float) pos.z).color((float) rgb.x, (float) rgb.y, (float) rgb.z, this.params.alpha).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(j, k).normal(peek.getNormal(), (float) xOffset, (float) yOffset, (float) zOffset).endVertex();
		this.transformNormals = null;
	}

	public OutlineParams getParams() {
		return this.params;
	}

	public static class OutlineParams {
		protected final boolean fadeLineWidth;
		protected final int lightMapU;
		protected final int lightMapV;
		protected Optional<ExtraTextures> faceTexture;
		protected Optional<ExtraTextures> hightlightedFaceTexture;
		protected Direction highlightedFace;
		protected boolean disableCull;
		protected boolean disableNormals;
		protected float alpha;
		protected Vec3d rgb;
		private float lineWidth;

		public OutlineParams() {
			this.faceTexture = this.hightlightedFaceTexture = Optional.empty();
			this.alpha = 1.0F;
			this.lineWidth = 0.03125F;
			this.fadeLineWidth = true;
			this.rgb = ColorHelper.getRGB(16777215);
			int i = 15728880;
			this.lightMapU = i >> 16 & '\uffff';
			this.lightMapV = i & '\uffff';
		}

		public OutlineParams colored(int color) {
			this.rgb = ColorHelper.getRGB(color);
			return this;
		}

		public OutlineParams lineWidth(float width) {
			this.lineWidth = width;
			return this;
		}

		public OutlineParams withFaceTexture(ExtraTextures texture) {
			this.faceTexture = Optional.ofNullable(texture);
			return this;
		}

		public void clearTextures() {
			this.withFaceTextures(null, null);
		}

		public void withFaceTextures(ExtraTextures texture, ExtraTextures highlightTexture) {
			this.faceTexture = Optional.ofNullable(texture);
			this.hightlightedFaceTexture = Optional.ofNullable(highlightTexture);
		}

		public OutlineParams highlightFace(@Nullable Direction face) {
			this.highlightedFace = face;
			return this;
		}

		public OutlineParams disableNormals() {
			this.disableNormals = true;
			return this;
		}

		public OutlineParams disableCull() {
			this.disableCull = true;
			return this;
		}

		public float getLineWidth() {
			return this.fadeLineWidth ? this.alpha * this.lineWidth : this.lineWidth;
		}

		public Direction getHighlightedFace() {
			return this.highlightedFace;
		}
	}
}