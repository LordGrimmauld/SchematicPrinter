package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public class PlacementToolBase extends SchematicToolBase {
	@Override
	public void init() {
		super.init();
	}

	@Override
	public void updateSelection() {
		super.updateSelection();
	}

	@Override
	public void renderTool(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		super.renderTool(ms, buffer);
	}

	@Override
	public void renderOverlay(MatrixStack ms, IRenderTypeBuffer buffer) {
		super.renderOverlay(ms, buffer);
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		return false;
	}

	@Override
	public boolean handleActivated() {
		return false;
	}
}
