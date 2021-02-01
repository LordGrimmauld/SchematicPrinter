package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.util.VecHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;

public class MoveTool extends PlacementToolBase {
	@Override
	public void init() {
		super.init();
		renderSelectedFace = true;
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (!schematicSelected || !selectedFace.getAxis().isHorizontal() || inf == null)
			return false;

		Vec3d vec = new Vec3d(selectedFace.getDirectionVec()).scale(-Math.signum(delta));
		vec = vec.mul(inf.transformation.getMirrorModifier(Direction.Axis.X), 1, inf.transformation.getMirrorModifier(Direction.Axis.Z));
		vec = VecHelper.rotate(vec, inf.transformation.getRotationTarget(), Direction.Axis.Y);
		inf.transformation.move((float) vec.x, 0, (float) vec.z);
		return true;
	}
}
