package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.world.phys.Vec3;

public class LineOutline extends Outline {
	protected Vec3 start;
	protected Vec3 end;

	public LineOutline() {
		this.start = Vec3.ZERO;
		this.end = Vec3.ZERO;
	}

	public LineOutline set(Vec3 start, Vec3 end) {
		this.start = start;
		this.end = end;
		return this;
	}

	public void render(PoseStack ms, SuperRenderTypeBuffer buffer) {
		this.renderCuboidLine(ms, buffer, this.start, this.end);
	}
}
