package mod.grimmauld.schematicprinter.client;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectOption;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SchematicSelectConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SchematicPrinterClient {
	public static KeyBinding TOOL_CONFIG;
	public static KeyBinding MENU_BUTTON;
	public static KeyBinding TOOL_SELECT;
	public static KeyBinding TOOL_SUBMIT;

	public static void init() {
		MENU_BUTTON = new KeyBinding("open Menu", Keyboard.O.getKeycode(), SchematicPrinter.NAME);
		TOOL_SELECT = new KeyBinding("Tool Select", Keyboard.LALT.getKeycode(), SchematicPrinter.NAME);
		TOOL_SUBMIT = new KeyBinding("Tool Submit", Keyboard.ENTER.getKeycode(), SchematicPrinter.NAME);
		TOOL_CONFIG = new KeyBinding("Tool Config", Keyboard.CTRL.getKeycode(), SchematicPrinter.NAME);
		ClientRegistry.registerKeyBinding(MENU_BUTTON);
		ClientRegistry.registerKeyBinding(TOOL_SELECT);
		ClientRegistry.registerKeyBinding(TOOL_SUBMIT);
		ClientRegistry.registerKeyBinding(TOOL_CONFIG);

		SelectOverlay schematicOverlay = new SelectOverlay(SchematicPrinterClient.MENU_BUTTON, "Schematics")
			.addOption(new SelectOption(null, "test"))
			.addOption(new SchematicSelectConfig("Selected Schematic"))
			.register();

		SelectOverlay overlayMain = new SelectOverlay(SchematicPrinterClient.MENU_BUTTON, new StringTextComponent("test"))
			.configureDirectOpen(true)
			.addOption(new SelectOption(null, "test"))
			.addOption(new BooleanSelectConfig("testBoolean", false))
			.addOption(new IntSelectConfig("testInt", 0, 42, 100))
			.addOption(new SelectOpenOverlay("Schematics", schematicOverlay))
			.register();
	}
}
