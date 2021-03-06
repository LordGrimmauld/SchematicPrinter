package mod.grimmauld.schematicprinter.client;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.palette.PaletteOverlay;
import mod.grimmauld.schematicprinter.util.KeybindHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class Manager {
	public static final boolean shouldCloseOnEsc = false;
	public static final Set<SelectOverlay> overlays = new HashSet<>();
	public static final PaletteOverlay paletteOverlay = new PaletteOverlay();

	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (event.getAction() != Keyboard.PRESS)
			return;
		testKeybinds(event);
	}

	private static void testKeybinds(InputEvent event) {
		Optional<SelectOverlay> activeOverlay = getActiveOverlay();
		if (KeybindHelper.eventActivatesKeybind(event, SchematicPrinterClient.TOOL_ACTIVATE)) {
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
		getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).filter(SelectItem::shouldRenderPalette).ifPresent(selectItem -> paletteOverlay.render(event));
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
