package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.overlay.selection.IOverlayEventListener;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public interface ISchematicTool extends IOverlayEventListener {
	void init();

	void updateSelection();

	boolean handleActivated();

	boolean handleMouseWheel(double delta);

	void renderTool(MatrixStack ms, SuperRenderTypeBuffer buffer);

	void renderOverlay(MatrixStack ms, IRenderTypeBuffer buffer);

	void renderOnSchematic(MatrixStack ms, SuperRenderTypeBuffer buffer);

}
