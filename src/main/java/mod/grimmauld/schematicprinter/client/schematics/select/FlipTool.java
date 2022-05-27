package mod.grimmauld.schematicprinter.client.schematics.select;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlipTool extends SchematicToolBase {

	private final AABBOutline outline = new AABBOutline(new AxisAlignedBB(BlockPos.ZERO));

	public FlipTool(ITextComponent description) {
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
	public void renderOnSchematic(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (!schematicSelected || !selectedFace.getAxis()
			.isHorizontal() || inf == null || !schematicHandler.isDeployed()) {
			super.renderOnSchematic(ms, buffer);
			return;
		}

		Direction facing = selectedFace.getClockWise();
		AxisAlignedBB bounds = inf.bounds;

		Vector3d directionVec = Vector3d.atLowerCornerOf(Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis())
			.getNormal());
		Vector3d boundsSize = new Vector3d(bounds.getXsize(), bounds.getYsize(), bounds.getZsize());
		Vector3d vec = boundsSize.multiply(directionVec);
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
