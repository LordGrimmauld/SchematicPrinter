package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;

public class InstantPrintTool extends SchematicToolBase {
	@Override
	public boolean handleActivated() {
		SchematicPrinterClient.schematicHandler.printInstantly();
		return true;
	}
}
