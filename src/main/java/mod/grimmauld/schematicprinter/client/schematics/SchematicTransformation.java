package mod.grimmauld.schematicprinter.client.schematics;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.util.VecHelper;
import mod.grimmauld.schematicprinter.util.interpolation.InterpolatedChasingAngle;
import mod.grimmauld.schematicprinter.util.interpolation.InterpolatedChasingValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.template.PlacementSettings;

public class SchematicTransformation {
	private final InterpolatedChasingValue x = new InterpolatedChasingValue();
	private final InterpolatedChasingValue y = new InterpolatedChasingValue();
	private final InterpolatedChasingValue z = new InterpolatedChasingValue();
	private final InterpolatedChasingValue scaleFrontBack = new InterpolatedChasingValue();
	private final InterpolatedChasingValue scaleLeftRight = new InterpolatedChasingValue();
	private final InterpolatedChasingAngle rotation = new InterpolatedChasingAngle();
	private double xOrigin;
	private double zOrigin;

	public SchematicTransformation() {
	}

	public void init(BlockPos anchor, PlacementSettings settings, AxisAlignedBB bounds) {
		int leftRight = settings.getMirror() == Mirror.LEFT_RIGHT ? -1 : 1;
		int frontBack = settings.getMirror() == Mirror.FRONT_BACK ? -1 : 1;
		this.getScaleFB().setStart((float) frontBack);
		this.getScaleLR().setStart((float) leftRight);
		this.xOrigin = bounds.getXSize() / 2.0D;
		this.zOrigin = bounds.getZSize() / 2.0D;
		int r = -(settings.getRotation().ordinal() * 90);
		this.rotation.setStart((float) r);
		Vec3d vec = this.fromAnchor(anchor);
		this.x.setStart((float) vec.x);
		this.y.setStart((float) vec.y);
		this.z.setStart((float) vec.z);
	}

	public void applyGLTransformations(MatrixStack ms) {
		float pt = Minecraft.getInstance().getRenderPartialTicks();
		ms.translate(this.x.get(pt), this.y.get(pt), this.z.get(pt));
		Vec3d rotationOffset = this.getRotationOffset(true);
		float fb = this.getScaleFB().get(pt);
		float lr = this.getScaleLR().get(pt);
		float rot = this.rotation.get(pt) + (float) (fb < 0.0F && lr < 0.0F ? 180 : 0);
		ms.translate(this.xOrigin, 0.0D, this.zOrigin);
		ms.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
		ms.rotate(Vector3f.YP.rotationDegrees(rot));
		ms.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
		ms.scale(Math.abs(fb), 1.0F, Math.abs(lr));
		ms.translate(-this.xOrigin, 0.0D, -this.zOrigin);
	}

	public Vec3d getRotationOffset(boolean ignoreMirrors) {
		Vec3d rotationOffset = Vec3d.ZERO;
		if ((int) (this.zOrigin * 2.0D) % 2 != (int) (this.xOrigin * 2.0D) % 2) {
			boolean xGreaterZ = this.xOrigin > this.zOrigin;
			float xIn = xGreaterZ ? 0.0F : 0.5F;
			float zIn = !xGreaterZ ? 0.0F : 0.5F;
			if (!ignoreMirrors) {
				xIn *= (float) this.getMirrorModifier(Direction.Axis.X);
				zIn *= (float) this.getMirrorModifier(Direction.Axis.Z);
			}

			rotationOffset = new Vec3d(xIn, 0.0D, zIn);
		}

		return rotationOffset;
	}

	public Vec3d toLocalSpace(Vec3d vec) {
		float pt = Minecraft.getInstance().getRenderPartialTicks();
		Vec3d rotationOffset = this.getRotationOffset(true);
		vec = vec.subtract(this.x.get(pt), this.y.get(pt), this.z.get(pt));
		vec = vec.subtract(this.xOrigin + rotationOffset.x, 0.0D, this.zOrigin + rotationOffset.z);
		vec = VecHelper.rotate(vec, -this.rotation.get(pt), Direction.Axis.Y);
		vec = vec.add(rotationOffset.x, 0.0D, rotationOffset.z);
		vec = vec.mul(this.getScaleFB().get(pt), 1.0D, this.getScaleLR().get(pt));
		vec = vec.add(this.xOrigin, 0.0D, this.zOrigin);
		return vec;
	}

	public PlacementSettings toSettings() {
		PlacementSettings settings = new PlacementSettings();
		int i = (int) this.rotation.getTarget();
		boolean mirrorlr = this.getScaleLR().getTarget() < 0.0F;
		boolean mirrorfb = this.getScaleFB().getTarget() < 0.0F;
		if (mirrorlr && mirrorfb) {
			mirrorfb = false;
			mirrorlr = false;
			i += 180;
		}

		i %= 360;
		if (i < 0) {
			i += 360;
		}

		Rotation rotation = Rotation.NONE;
		switch (i) {
			case 90:
				rotation = Rotation.COUNTERCLOCKWISE_90;
				break;
			case 180:
				rotation = Rotation.CLOCKWISE_180;
				break;
			case 270:
				rotation = Rotation.CLOCKWISE_90;
		}

		settings.setRotation(rotation);
		if (mirrorfb) {
			settings.setMirror(Mirror.FRONT_BACK);
		}

		if (mirrorlr) {
			settings.setMirror(Mirror.LEFT_RIGHT);
		}

		return settings;
	}

	public BlockPos getAnchor() {
		Vec3d vec = Vec3d.ZERO.add(0.5D, 0.0D, 0.5D);
		Vec3d rotationOffset = this.getRotationOffset(false);
		vec = vec.subtract(this.xOrigin, 0.0D, this.zOrigin);
		vec = vec.subtract(rotationOffset.x, 0.0D, rotationOffset.z);
		vec = vec.mul(this.getScaleFB().getTarget(), 1.0D, this.getScaleLR().getTarget());
		vec = VecHelper.rotate(vec, this.rotation.getTarget(), Direction.Axis.Y);
		vec = vec.add(this.xOrigin, 0.0D, this.zOrigin);
		vec = vec.add(this.x.getTarget(), this.y.getTarget(), this.z.getTarget());
		return new BlockPos(vec.x, vec.y, vec.z);
	}

	public Vec3d fromAnchor(BlockPos pos) {
		Vec3d vec = Vec3d.ZERO.add(0.5D, 0.0D, 0.5D);
		Vec3d rotationOffset = this.getRotationOffset(false);
		vec = vec.subtract(this.xOrigin, 0.0D, this.zOrigin);
		vec = vec.subtract(rotationOffset.x, 0.0D, rotationOffset.z);
		vec = vec.mul(this.getScaleFB().getTarget(), 1.0D, this.getScaleLR().getTarget());
		vec = VecHelper.rotate(vec, this.rotation.getTarget(), Direction.Axis.Y);
		vec = vec.add(this.xOrigin, 0.0D, this.zOrigin);
		return new Vec3d(pos.subtract(new BlockPos(vec.x, vec.y, vec.z)));
	}

	public int getRotationTarget() {
		return (int) this.rotation.getTarget();
	}

	public int getMirrorModifier(Direction.Axis axis) {
		return axis == Direction.Axis.Z ? (int) this.getScaleLR().getTarget() : (int) this.getScaleFB().getTarget();
	}

	public float getCurrentRotation() {
		float pt = Minecraft.getInstance().getRenderPartialTicks();
		return this.rotation.get(pt);
	}

	public void tick() {
		this.x.tick();
		this.y.tick();
		this.z.tick();
		this.getScaleLR().tick();
		this.getScaleFB().tick();
		this.rotation.tick();
	}

	public void flip(Direction.Axis axis) {
		if (axis == Direction.Axis.X) {
			this.getScaleLR().setTarget(this.getScaleLR().getTarget() * -1.0F);
		}

		if (axis == Direction.Axis.Z) {
			this.getScaleFB().setTarget(this.getScaleFB().getTarget() * -1.0F);
		}

	}

	public void rotate90(boolean clockwise) {
		this.rotation.setTarget(this.rotation.getTarget() + (float) (clockwise ? -90 : 90));
	}

	public void move(float xIn, float yIn, float zIn) {
		this.moveTo(this.x.getTarget() + xIn, this.y.getTarget() + yIn, this.z.getTarget() + zIn);
	}

	public void moveTo(BlockPos pos) {
		this.moveTo((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
	}

	public void moveTo(float xIn, float yIn, float zIn) {
		this.x.setTarget(xIn);
		this.y.setTarget(yIn);
		this.z.setTarget(zIn);
	}

	public InterpolatedChasingValue getScaleFB() {
		return this.scaleFrontBack;
	}

	public InterpolatedChasingValue getScaleLR() {
		return this.scaleLeftRight;
	}
}
