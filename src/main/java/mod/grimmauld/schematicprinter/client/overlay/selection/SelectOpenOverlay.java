package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOpenOverlay extends SelectItem {

	private final SelectOverlay toOpen;

	public SelectOpenOverlay(ITextComponent description, SelectOverlay toOpen) {
		super(description, null);
		this.toOpen = toOpen;
	}

	public SelectOpenOverlay(String description, SelectOverlay toOpen) {
		this(new TranslationTextComponent(description), toOpen);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		toOpen.open(screen);
	}
}
