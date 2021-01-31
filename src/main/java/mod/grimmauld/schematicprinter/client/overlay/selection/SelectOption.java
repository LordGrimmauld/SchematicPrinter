package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOption extends SelectItem {
	public SelectOption(@Nullable Consumer<SelectOverlay> onInvoke, String desc) {
		super(desc, onInvoke == null ? overlay -> System.out.println(desc) : onInvoke);
	}

	@Override
	public void invoke(SelectOverlay screen) {
		screen.close();
		super.invoke(screen);
	}
}
