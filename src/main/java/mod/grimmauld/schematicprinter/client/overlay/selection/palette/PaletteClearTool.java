package mod.grimmauld.schematicprinter.client.overlay.selection.palette;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.BuildToolStateSupplier;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PaletteClearTool extends SelectItem {
	public PaletteClearTool(ITextComponent description) {
		super(description);
	}

	public PaletteClearTool(String description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		PaletteManager.clearPalette();
	}

	@Override
	public boolean shouldRenderPalette() {
		return true;
	}
}
