package mod.grimmauld.schematicprinter.client;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.api.Keyboard;
import mod.grimmauld.schematicprinter.client.api.RegisterOverlayEvent;
import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.util.KeybindHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static mod.grimmauld.schematicprinter.util.TextHelper.translationComponent;
import static mod.grimmauld.schematicprinter.util.TextHelper.translationKey;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class Manager {
	public static final boolean shouldCloseOnEsc = false;
	public static final Set<SelectOverlay> overlays = new HashSet<>();

	public static KeyBinding TOOL_CONFIG;
	public static KeyBinding TOOL_DEACTIVATE;
	public static KeyBinding TOOL_SELECT;
	public static KeyBinding TOOL_ACTIVATE;

	public static void init(FMLClientSetupEvent event) {
		TOOL_DEACTIVATE = new KeyBinding(translationKey("keybind.menu"), Keyboard.O.getKeycode(), SchematicPrinter.NAME);
		TOOL_SELECT = new KeyBinding(translationKey("keybind.select_tool"), Keyboard.LALT.getKeycode(), SchematicPrinter.NAME);
		TOOL_ACTIVATE = new KeyBinding(translationKey("keybind.activate_tool"), Keyboard.ENTER.getKeycode(), SchematicPrinter.NAME);
		TOOL_CONFIG = new KeyBinding(translationKey("keybind.config"), Keyboard.CTRL.getKeycode(), SchematicPrinter.NAME);

		ClientRegistry.registerKeyBinding(TOOL_DEACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_SELECT);
		ClientRegistry.registerKeyBinding(TOOL_ACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_CONFIG);

		SelectOverlay overlayMain = new SelectOverlay(translationComponent("overlay.main"))
			.configureDirectOpen(true);
		FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterOverlayEvent(overlayMain));
		overlayMain.register();
	}

	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (event.getAction() != Keyboard.PRESS)
			return;
		testKeybinds(event);
	}

	private static void testKeybinds(InputEvent event) {
		Optional<SelectOverlay> activeOverlay = getActiveOverlay();
		if (KeybindHelper.eventActivatesKeybind(event, TOOL_ACTIVATE)) {
			SelectOverlay activeSelectOverlay = activeOverlay.orElse(null);
			if (activeSelectOverlay != null) {
				activeSelectOverlay.select();
				return;
			} else {
				for (SelectOverlay overlay : overlays) {
					if (overlay.testAndOpenDirectly())
						return;
				}
			}
		}

		activeOverlay.ifPresent(overlay -> overlay.testAndClose(event));
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		overlays.forEach(SelectOverlay::onClientTick);
	}

	@SubscribeEvent
	public static void onDrawGameOverlay(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		overlays.stream().filter(SelectOverlay::isVisible).forEach(selectScreen -> selectScreen.render(event));
		getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.renderExtra(event));
	}

	@SubscribeEvent
	public static void onMouseScrolled(InputEvent.MouseScrollEvent event) {
		if (Minecraft.getInstance().currentScreen != null)
			return;
		overlays.stream().filter(SelectOverlay::isVisible).forEach(overlay -> overlay.onScroll(event));
	}

	@SubscribeEvent
	public static void onMouseInput(InputEvent.MouseInputEvent event) {
		if (Minecraft.getInstance().currentScreen != null)
			return;

		int button = event.getButton();
		boolean pressed = !(event.getAction() == 0);

		if (!pressed)
			return;

		Minecraft mc = Minecraft.getInstance();
		if (button == mc.gameSettings.keyBindUseItem.getKey().getKeyCode()) {
			if (mc.world == null || mc.player == null || mc.player.isSneaking())
				return;
			getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.onRightClick(event));
		}
		testKeybinds(event);
	}

	@SubscribeEvent
	public static void onPlayerJoinWorld(ClientPlayerNetworkEvent.LoggedInEvent event) {
		SchematicPrinterClient.schematicHandler.quitSchematic();
	}

	public static Optional<SelectOverlay> getActiveOverlay() {
		return overlays.stream().filter(SelectOverlay::isVisible).findFirst();
	}
}
