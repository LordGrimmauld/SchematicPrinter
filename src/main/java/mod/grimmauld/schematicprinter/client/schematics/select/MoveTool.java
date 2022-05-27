package mod.grimmauld.schematicprinter.client.schematics.select;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.util.VecHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public class MoveTool extends SchematicToolBase {
	public MoveTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		renderSelectedFace = true;
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (!schematicSelected || !selectedFace.getAxis().isHorizontal() || inf == null)
			return false;

		Vector3d vec = Vector3d.atLowerCornerOf(selectedFace.getNormal()).scale(-Math.signum(delta));
		vec = vec.multiply(inf.transformation.getMirrorModifier(Direction.Axis.X), 1, inf.transformation.getMirrorModifier(Direction.Axis.Z));
		vec = VecHelper.rotate(vec, inf.transformation.getRotationTarget(), Direction.Axis.Y);
		inf.transformation.move((float) vec.x, 0, (float) vec.z);
		return true;
	}
}
