package mod.grimmauld.schematicprinter.client.schematics.select;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InstantPrintTool extends SchematicToolBase {
	public InstantPrintTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		SchematicPrinterClient.schematicHandler.printInstantly();
	}
}
