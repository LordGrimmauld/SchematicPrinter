package mod.grimmauld.schematicprinter.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KeybindHelper {
	public static boolean eventActivatesKeybind(InputEvent event, @Nullable KeyBinding key) {
		if (key == null)
			return false;
		if (event instanceof InputEvent.KeyInputEvent) {
			InputEvent.KeyInputEvent keyEvent = ((InputEvent.KeyInputEvent) event);
			return key.isActiveAndMatches(InputMappings.getInputByCode(keyEvent.getKey(), keyEvent.getScanCode()));
		}
		if (event instanceof InputEvent.MouseInputEvent) {
			InputEvent.MouseInputEvent mouseEvent = ((InputEvent.MouseInputEvent) event);
			return key.matchesMouseKey(mouseEvent.getButton());
		}
		return false;
	}
}
