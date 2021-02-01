package mod.grimmauld.schematicprinter.client;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class Manager {
	public static Set<SelectOverlay> overlays = new HashSet<>();


	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (event.getAction() != Keyboard.PRESS)
			return;

		Optional<SelectOverlay> activeOverlay = getActiveOverlay();

		if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
			boolean closed = false;

			// Close all to get rid of a glitched state
			for (SelectOverlay overlay : overlays) {
				if (overlay.isVisible()) {
					overlay.close();
					closed = true;
				}
			}
			if (closed)
				Minecraft.getInstance().displayGuiScreen(null); // Cancel pause screen
			return;
		}

		if (event.getKey() == SchematicPrinterClient.TOOL_SUBMIT.getKey().getKeyCode() && SchematicPrinterClient.TOOL_SUBMIT.isKeyDown()) {
			activeOverlay.ifPresent(SelectOverlay::select);
			return;
		}

		if (!activeOverlay.isPresent()) {
			for (SelectOverlay overlay : overlays) {
				if (overlay.testAndOpen(null))
					return;
			}
		}

		activeOverlay.ifPresent(overlay -> overlay.testAndClose(event.getKey()));
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

		if (!pressed || button != 1)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.world == null || mc.player == null || mc.player.isSneaking())
			return;
		getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.onRightClick(event));
	}

	@SubscribeEvent
	// FIXME: Client only equivalent!
	public static void onPlayerLeaveWorld(PlayerEvent.PlayerLoggedInEvent event) {
		SchematicPrinterClient.schematicHandler.quitSchematic();
	}

	public static Optional<SelectOverlay> getActiveOverlay() {
		return overlays.stream().filter(SelectOverlay::isVisible).findFirst();
	}
}
