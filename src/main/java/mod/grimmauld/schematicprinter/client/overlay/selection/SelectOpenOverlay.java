package mod.grimmauld.schematicprinter.client.overlay.selection;

import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;

public class SelectOpenOverlay extends SelectItem {

	public SelectOpenOverlay(ITextComponent description, SelectOverlay toOpen) {
		super(description, toOpen::open);
	}

	public SelectOpenOverlay(String description, SelectOverlay toOpen) {
		super(description, toOpen::open);
	}
}
