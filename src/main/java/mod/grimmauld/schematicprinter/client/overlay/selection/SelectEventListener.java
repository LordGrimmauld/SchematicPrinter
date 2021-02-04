package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectEventListener extends SelectItem {
	public final IOverlayEventListener listener;

	public SelectEventListener(ITextComponent description, IOverlayEventListener listener) {
		super(description);
		this.listener = listener;
	}

	public SelectEventListener(String description, IOverlayEventListener listener) {
		super(description);
		this.listener = listener;
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		listener.init();
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		listener.onEnter(screen);
	}

	@Override
	public void onScroll(InputEvent.MouseScrollEvent event) {
		super.onScroll(event);
		listener.onScroll(event);
	}

	@Override
	public void onRightClick(InputEvent.MouseInputEvent event) {
		super.onRightClick(event);
		listener.onRightClick(event);
	}
}
