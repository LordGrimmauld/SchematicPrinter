package mod.grimmauld.schematicprinter.client.schematics.select;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.sidebaroverlay.util.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

public class MoveTool extends SchematicToolBase {
	public MoveTool(Component description) {
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

		Vec3 vec = Vec3.atLowerCornerOf(selectedFace.getNormal()).scale(-Math.signum(delta));
		vec = vec.multiply(inf.transformation.getMirrorModifier(Direction.Axis.X), 1, inf.transformation.getMirrorModifier(Direction.Axis.Z));
		vec = VecHelper.rotate(vec, inf.transformation.getRotationTarget(), Direction.Axis.Y);
		inf.transformation.move((float) vec.x, 0, (float) vec.z);
		return true;
	}
}
