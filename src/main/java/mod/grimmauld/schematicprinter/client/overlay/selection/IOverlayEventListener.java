package mod.grimmauld.schematicprinter.client.overlay.selection;

import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraftforge.client.event.InputEvent;

public interface IOverlayEventListener {
	default void onEnter(SelectOverlay overlay) {
	}

	default void onScroll(InputEvent.MouseScrollEvent event) {
		handleMouseWheel(event.getScrollDelta());
	}

	default void onRightClick(InputEvent.MouseInputEvent event) {
		handleRightClick();
	}

	default boolean handleRightClick() {
		return true;
	}

	default boolean handleMouseWheel(double delta) {
		return true;
	}

	default void init() {}

}
