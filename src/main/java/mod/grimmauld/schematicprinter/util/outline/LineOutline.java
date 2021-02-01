package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LineOutline extends Outline {
	protected Vec3d start;
	protected Vec3d end;

	public LineOutline() {
		this.start = Vec3d.ZERO;
		this.end = Vec3d.ZERO;
	}

	public LineOutline set(Vec3d start, Vec3d end) {
		this.start = start;
		this.end = end;
		return this;
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		this.renderCuboidLine(ms, buffer, this.start, this.end);
	}

	public static class EndChasingLineOutline extends LineOutline {
		float prevProgress = 0.0F;
		float progress = 0.0F;

		public EndChasingLineOutline() {
		}

		public void tick() {
		}

		public LineOutline.EndChasingLineOutline setProgress(float progress) {
			this.prevProgress = this.progress;
			this.progress = progress;
			return this;
		}

		public LineOutline set(Vec3d start, Vec3d end) {
			if (!end.equals(this.end)) {
				super.set(start, end);
			}

			return this;
		}

		public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
			float pt = Minecraft.getInstance().getRenderPartialTicks();
			float distanceToTarget = 1.0F - MathHelper.lerp(pt, this.prevProgress, this.progress);
			Vec3d start = this.end.add(this.start.subtract(this.end).scale(distanceToTarget));
			this.renderCuboidLine(ms, buffer, start, this.end);
		}
	}
}
