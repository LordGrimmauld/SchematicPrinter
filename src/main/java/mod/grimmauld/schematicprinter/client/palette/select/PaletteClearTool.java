package mod.grimmauld.schematicprinter.client.palette.select;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PaletteClearTool extends SelectItem {
	public PaletteClearTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		PaletteManager.clearPalette();
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);
		SchematicPrinterClient.paletteOverlay.render(event);
	}
}
