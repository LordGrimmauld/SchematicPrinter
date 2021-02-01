package mod.grimmauld.schematicprinter.client.overlay.selection;

import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraftforge.client.event.InputEvent;

public interface IOverlayEventListener {
	default void onEnter(SelectOverlay overlay) {
		handleActivated();
	}

	default void onScroll(InputEvent.MouseScrollEvent event) {
		if (SchematicPrinterClient.TOOL_CONFIG.isKeyDown())
			event.setCanceled(handleMouseWheel(event.getScrollDelta()));
	}

	default void onRightClick(InputEvent.MouseInputEvent event) {
		// handleActivated();
	}

	default boolean handleActivated() {
		return true;
	}

	default boolean handleMouseWheel(double delta) {
		return false;
	}

	default void init() {
	}

}
