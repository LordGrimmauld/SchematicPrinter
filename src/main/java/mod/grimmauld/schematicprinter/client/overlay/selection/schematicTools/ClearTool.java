package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;

public class ClearTool extends SchematicToolBase {
	@Override
	public boolean handleActivated() {
		SchematicPrinterClient.schematicHandler.quitSchematic();
		return true;
	}
}
