package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
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
}
