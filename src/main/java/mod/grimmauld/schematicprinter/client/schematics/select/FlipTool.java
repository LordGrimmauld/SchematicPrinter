package mod.grimmauld.schematicprinter.client.schematics.select;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlipTool extends SchematicToolBase {

	private final AABBOutline outline = new AABBOutline(new AABB(BlockPos.ZERO));

	public FlipTool(Component description) {
		super(description);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		renderSelectedFace = false;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		mirror();
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		mirror();
		return true;
	}

	private void mirror() {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (inf != null && schematicSelected && selectedFace.getAxis()
			.isHorizontal()) {
			inf.transformation
				.flip(selectedFace.getAxis());
		}
	}

	@Override
	public void renderOnSchematic(PoseStack ms, SuperRenderTypeBuffer buffer) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (!schematicSelected || !selectedFace.getAxis()
			.isHorizontal() || inf == null || !schematicHandler.isDeployed()) {
			super.renderOnSchematic(ms, buffer);
			return;
		}

		Direction facing = selectedFace.getClockWise();
		AABB bounds = inf.bounds;

		Vec3 directionVec = Vec3.atLowerCornerOf(Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis())
			.getNormal());
		Vec3 boundsSize = new Vec3(bounds.getXsize(), bounds.getYsize(), bounds.getZsize());
		Vec3 vec = boundsSize.multiply(directionVec);
		bounds = bounds.contract(vec.x, vec.y, vec.z)
			.inflate(1 - directionVec.x, 1 - directionVec.y, 1 - directionVec.z);
		bounds = bounds.move(directionVec.scale(.5f)
			.multiply(boundsSize));

		outline.setBounds(bounds);
		ExtraTextures tex = ExtraTextures.CHECKERED;
		outline.getParams()
			.lineWidth(1 / 16f)
			.disableNormals()
			.colored(0xdddddd)
			.withFaceTextures(tex, tex);
		outline.render(ms, buffer);

		super.renderOnSchematic(ms, buffer);
	}
}
