package mod.grimmauld.schematicprinter.client;

import com.simibubi.mightyarchitect.foundation.utility.Keyboard;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
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
		int amount = (int) Math.signum(event.getScrollDelta());

		if (SchematicPrinterClient.TOOL_SELECT.isKeyDown()) {
			event.setCanceled(overlays.stream().filter(SelectOverlay::isVisible).map(overlay -> {
				overlay.advanceSelectionIndex(amount);
				return true;
			}).findFirst().orElse(false));
		} else if (SchematicPrinterClient.TOOL_CONFIG.isKeyDown()) {
			event.setCanceled(overlays.stream().filter(SelectOverlay::isVisible).map(overlay -> {
				Optional<SelectConfig> config = overlay.getActiveSelectConfig();
				config.ifPresent(selectConfig -> selectConfig.onScrolled(amount));
				return config.isPresent();
			}).findFirst().orElse(false));
		}
	}

	public static Optional<SelectOverlay> getActiveOverlay() {
		return overlays.stream().filter(SelectOverlay::isVisible).findFirst();
	}
}
