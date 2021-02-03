package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class FlipTool extends PlacementToolBase {
	private final AABBOutline outline = new AABBOutline(new AxisAlignedBB(BlockPos.ZERO));

	@Override
	public void init() {
		super.init();
		renderSelectedFace = false;
	}

	@Override
	public boolean handleActivated() {
		mirror();
		return true;
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

		Vector3d directionVec = Vector3d.copy(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, facing.getAxis())
			.getDirectionVec());
		Vector3d boundsSize = new Vector3d(bounds.getXSize(), bounds.getYSize(), bounds.getZSize());
		Vector3d vec = boundsSize.mul(directionVec);
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
