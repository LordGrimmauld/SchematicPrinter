package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	private final ITextComponent description;
	@Nullable
	public IOverlayEventListener listener;

	public SelectItem(ITextComponent description, @Nullable IOverlayEventListener listener) {
		this.listener = listener;
		this.description = description;
	}

	public SelectItem(String description, @Nullable IOverlayEventListener listener) {
		this(new TranslationTextComponent(description), listener);
	}

	public void onEnter(SelectOverlay screen) {
		if (listener != null)
			listener.onEnter(screen);
	}

	public void onOverlayOpen() {
		if (listener != null)
			listener.init();
	}

	public ITextComponent getDescription() {
		return description.deepCopy();
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
		if (listener != null)
			listener.onScroll(event);
	}

	public void onRightClick(InputEvent.MouseInputEvent event) {
		if (listener != null)
			listener.onRightClick(event);
	}
}
