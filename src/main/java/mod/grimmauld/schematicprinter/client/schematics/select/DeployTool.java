package mod.grimmauld.schematicprinter.client.schematics.select;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.sidebaroverlay.Manager.TOOL_CONFIG;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeployTool extends SchematicToolBase {
	public DeployTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		selectionRange = -1;
	}

	@Override
	public void updateSelection() {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (inf != null && schematicHandler.isActive() && selectionRange == -1) {
			selectionRange = (int) (inf.bounds
				.getCenter()
				.length() / 2);
			selectionRange = MathHelper.clamp(selectionRange, 1, 100);
		}
		selectIgnoreBlocks = TOOL_CONFIG.isPressed();
		super.updateSelection();
	}

	@Override
	public void renderTool(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		super.renderTool(ms, buffer);
		SchematicMetaInf inf = schematicHandler.activeSchematic;

		if (selectedPos == null || inf == null)
			return;

		ms.push();
		float pt = Minecraft.getInstance()
			.getRenderPartialTicks();
		double x = MathHelper.lerp(pt, lastChasingSelectedPos.x, chasingSelectedPos.x);
		double y = MathHelper.lerp(pt, lastChasingSelectedPos.y, chasingSelectedPos.y);
		double z = MathHelper.lerp(pt, lastChasingSelectedPos.z, chasingSelectedPos.z);

		Vector3d center = inf.bounds.getCenter();
		Vector3d rotationOffset = inf.transformation.getRotationOffset(true);
		int centerX = (int) center.x;
		int centerZ = (int) center.z;
		double xOrigin = inf.bounds.getXSize() / 2f;
		double zOrigin = inf.bounds.getZSize() / 2f;
		Vector3d origin = new Vector3d(xOrigin, 0, zOrigin);

		ms.translate(x - centerX, y, z - centerZ);
		ms.translate(origin.x, origin.y, origin.z);
		ms.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
		ms.rotate(Vector3f.YP.rotationDegrees(inf.transformation.getCurrentRotation()));
		ms.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
		ms.translate(-origin.x, -origin.y, -origin.z);
		inf.outline.render(ms, buffer);
		inf.outline.getParams()
			.clearTextures();
		ms.pop();
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		if (!selectIgnoreBlocks)
			return super.handleMouseWheel(delta);
		selectionRange += delta;
		selectionRange = MathHelper.clamp(selectionRange, 1, 100);
		return true;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (selectedPos == null || inf == null)
			return;
		Vector3d center = inf.bounds
			.getCenter();
		BlockPos target = selectedPos.add(-((int) center.x), 0, -((int) center.z));
		inf.transformation
			.moveTo(target);
		schematicHandler.deploy();
	}
}