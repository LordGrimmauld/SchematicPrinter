package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;

public class MoveVerticalTool extends PlacementToolBase {
	@Override
	public boolean handleMouseWheel(double delta) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (inf != null && schematicHandler.isDeployed()) {
			inf.transformation.move(0, (float) Math.signum(delta), 0);
			return true;
		}
		return false;
	}
}
