package mod.grimmauld.schematicprinter.client.api;

import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public class RegisterOverlayEvent extends Event implements IModBusEvent {
	public final SelectOverlay overlayMain;

	public RegisterOverlayEvent(SelectOverlay overlayMain) {
		this.overlayMain = overlayMain;
	}
}
