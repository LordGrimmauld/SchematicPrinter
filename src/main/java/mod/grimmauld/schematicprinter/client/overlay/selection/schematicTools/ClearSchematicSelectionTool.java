package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClearSchematicSelectionTool extends SchematicToolBase {
	public ClearSchematicSelectionTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		SchematicPrinterClient.schematicHandler.quitSchematic();
	}
}
