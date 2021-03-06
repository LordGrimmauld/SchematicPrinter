package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

		Direction facing = selectedFace.rotateY();
		AxisAlignedBB bounds = inf.bounds;

		Vec3d directionVec = new Vec3d(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, facing.getAxis())
			.getDirectionVec());
		Vec3d boundsSize = new Vec3d(bounds.getXSize(), bounds.getYSize(), bounds.getZSize());
		Vec3d vec = boundsSize.mul(directionVec);
		bounds = bounds.contract(vec.x, vec.y, vec.z)
			.grow(1 - directionVec.x, 1 - directionVec.y, 1 - directionVec.z);
		bounds = bounds.offset(directionVec.scale(.5f)
			.mul(boundsSize));

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
