package mod.grimmauld.schematicprinter.client.schematics.select;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InstantPrintTool extends SchematicToolBase {
	public InstantPrintTool(Component description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		SchematicPrinterClient.schematicHandler.printInstantly();
	}
}