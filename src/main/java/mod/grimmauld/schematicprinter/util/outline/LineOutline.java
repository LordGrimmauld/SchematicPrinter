package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class LineOutline extends Outline {
	protected Vector3d start;
	protected Vector3d end;

	public LineOutline() {
		this.start = Vector3d.ZERO;
		this.end = Vector3d.ZERO;
	}

	public LineOutline set(Vector3d start, Vector3d end) {
		this.start = start;
		this.end = end;
		return this;
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		this.renderCuboidLine(ms, buffer, this.start, this.end);
	}
}
