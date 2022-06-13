package mod.grimmauld.schematicprinter.client.schematics.select;

import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import net.minecraft.network.chat.Component;

public class MoveVerticalTool extends SchematicToolBase {
	public MoveVerticalTool(Component description) {
		super(description);
	}

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
