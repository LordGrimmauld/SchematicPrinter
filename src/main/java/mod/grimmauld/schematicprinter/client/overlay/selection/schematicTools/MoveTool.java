package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.util.VecHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

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

		Vector3d vec = Vector3d.copy(selectedFace.getDirectionVec()).scale(-Math.signum(delta));
		vec = vec.mul(inf.transformation.getMirrorModifier(Direction.Axis.X), 1, inf.transformation.getMirrorModifier(Direction.Axis.Z));
		vec = VecHelper.rotate(vec, inf.transformation.getRotationTarget(), Direction.Axis.Y);
		inf.transformation.move((float) vec.x, 0, (float) vec.z);
		return true;
	}
}
