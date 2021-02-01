package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOption extends SelectItem {
	public SelectOption(String desc) {
		super(desc, null);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		screen.close();
		super.onEnter(screen);
		System.out.println(getDescription().getUnformattedComponentText());
	}
}
