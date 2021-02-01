package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.grimmauld.schematicprinter.render.RenderTypes;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class AABBOutline extends Outline {
	protected AxisAlignedBB bb;

	public AABBOutline(AxisAlignedBB bb) {
		this.setBounds(bb);
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		this.renderBB(ms, buffer, this.bb);
	}

	public void renderBB(MatrixStack ms, SuperRenderTypeBuffer buffer, AxisAlignedBB bb) {
		Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
		boolean noCull = bb.contains(projectedView);
		bb = bb.grow(noCull ? -0.0078125D : 0.0078125D);
		noCull |= this.params.disableCull;
		Vector3d xyz = new Vector3d(bb.minX, bb.minY, bb.minZ);
		Vector3d Xyz = new Vector3d(bb.maxX, bb.minY, bb.minZ);
		Vector3d xYz = new Vector3d(bb.minX, bb.maxY, bb.minZ);
		Vector3d XYz = new Vector3d(bb.maxX, bb.maxY, bb.minZ);
		Vector3d xyZ = new Vector3d(bb.minX, bb.minY, bb.maxZ);
		Vector3d XyZ = new Vector3d(bb.maxX, bb.minY, bb.maxZ);
		Vector3d xYZ = new Vector3d(bb.minX, bb.maxY, bb.maxZ);
		Vector3d XYZ = new Vector3d(bb.maxX, bb.maxY, bb.maxZ);
		renderPartial(ms, buffer, xYz, Xyz, xyz, XYZ, XyZ, xyZ);
		renderPartial(ms, buffer, Xyz, xYz, XYz, xyZ, xYZ, XYZ);
		this.renderFace(ms, buffer, Direction.NORTH, xYz, XYz, Xyz, xyz, noCull);
		this.renderFace(ms, buffer, Direction.SOUTH, XYZ, xYZ, xyZ, XyZ, noCull);
		this.renderFace(ms, buffer, Direction.EAST, XYz, XYZ, XyZ, Xyz, noCull);
		this.renderFace(ms, buffer, Direction.WEST, xYZ, xYz, xyz, xyZ, noCull);
		this.renderFace(ms, buffer, Direction.UP, xYZ, XYZ, XYz, xYz, noCull);
		this.renderFace(ms, buffer, Direction.DOWN, xyz, Xyz, XyZ, xyZ, noCull);
	}

	private void renderPartial(MatrixStack ms, SuperRenderTypeBuffer buffer, Vector3d xyz, Vector3d xYz, Vector3d XYz, Vector3d xyZ, Vector3d xYZ, Vector3d XYZ) {
		this.renderAACuboidLine(ms, buffer, XYz, xYz);
		this.renderAACuboidLine(ms, buffer, XYz, xyz);
		this.renderAACuboidLine(ms, buffer, XYz, XYZ);
		this.renderAACuboidLine(ms, buffer, xYZ, XYZ);
		this.renderAACuboidLine(ms, buffer, xYZ, xyZ);
		this.renderAACuboidLine(ms, buffer, xYZ, xYz);
	}

	protected void renderFace(MatrixStack ms, SuperRenderTypeBuffer buffer, Direction direction, Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4, boolean noCull) {
		if (this.params.faceTexture.isPresent()) {
			ResourceLocation faceTexture = this.params.faceTexture.get().getLocation();
			float alphaBefore = this.params.alpha;
			this.params.alpha = direction == this.params.getHighlightedFace() && this.params.hightlightedFaceTexture.isPresent() ? 1.0F : 0.5F;
			RenderType translucentType = RenderTypes.getOutlineTranslucent(faceTexture, !noCull);
			IVertexBuilder builder = buffer.getLateBuffer(translucentType);
			Direction.Axis axis = direction.getAxis();
			Vector3d uDiff = p2.subtract(p1);
			Vector3d vDiff = p4.subtract(p1);
			float maxU = (float) Math.abs(axis == Direction.Axis.X ? uDiff.z : uDiff.x);
			float maxV = (float) Math.abs(axis == Direction.Axis.Y ? vDiff.z : vDiff.y);
			this.putQuadUV(ms, builder, p1, p2, p3, p4, 0.0F, 0.0F, maxU, maxV, Direction.UP);
			this.params.alpha = alphaBefore;
		}
	}

	public void setBounds(AxisAlignedBB bb) {
		this.bb = bb;
	}
}
