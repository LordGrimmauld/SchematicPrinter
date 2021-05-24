package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import net.minecraftforge.eventbus.api.Event;

public class SelectConfigChangedEvent<T extends Comparable<? super T>> extends Event {
	public final SelectConfig<T> config;

	public SelectConfigChangedEvent(SelectConfig<T> config) {
		this.config = config;
	}
}
