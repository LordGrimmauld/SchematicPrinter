package mod.grimmauld.schematicprinter.client.schematics.select;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.sidebaroverlay.Manager.TOOL_CONFIG;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeployTool extends SchematicToolBase {
	public DeployTool(Component description) {
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
			selectionRange = Mth.clamp(selectionRange, 1, 100);
		}
		selectIgnoreBlocks = TOOL_CONFIG.consumeClick();
		super.updateSelection();
	}

	@Override
	public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer) {
		super.renderTool(ms, buffer);
		SchematicMetaInf inf = schematicHandler.activeSchematic;

		if (selectedPos == null || inf == null)
			return;

		ms.pushPose();
		float pt = Minecraft.getInstance()
			.getFrameTime();
		double x = Mth.lerp(pt, lastChasingSelectedPos.x, chasingSelectedPos.x);
		double y = Mth.lerp(pt, lastChasingSelectedPos.y, chasingSelectedPos.y);
		double z = Mth.lerp(pt, lastChasingSelectedPos.z, chasingSelectedPos.z);

		Vec3 center = inf.bounds.getCenter();
		Vec3 rotationOffset = inf.transformation.getRotationOffset(true);
		int centerX = (int) center.x;
		int centerZ = (int) center.z;
		double xOrigin = inf.bounds.getXsize() / 2f;
		double zOrigin = inf.bounds.getZsize() / 2f;
		Vec3 origin = new Vec3(xOrigin, 0, zOrigin);

		ms.translate(x - centerX, y, z - centerZ);
		ms.translate(origin.x, origin.y, origin.z);
		ms.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
		ms.mulPose(Vector3f.YP.rotationDegrees(inf.transformation.getCurrentRotation()));
		ms.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
		ms.translate(-origin.x, -origin.y, -origin.z);
		inf.outline.render(ms, buffer);
		inf.outline.getParams()
			.clearTextures();
		ms.popPose();
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		if (!selectIgnoreBlocks)
			return super.handleMouseWheel(delta);
		selectionRange += delta;
		selectionRange = Mth.clamp(selectionRange, 1, 100);
		return true;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (selectedPos == null || inf == null)
			return;
		Vec3 center = inf.bounds
			.getCenter();
		BlockPos target = selectedPos.offset(-((int) center.x), 0, -((int) center.z));
		inf.transformation
			.moveTo(target);
		schematicHandler.deploy();
	}
}