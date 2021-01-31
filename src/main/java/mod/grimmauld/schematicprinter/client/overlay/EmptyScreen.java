package mod.grimmauld.schematicprinter.client.overlay;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class EmptyScreen extends Screen {
	public static final Screen INSTANCE = new EmptyScreen();

	private EmptyScreen() {
		super(new StringTextComponent("empty"));
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().currentScreen instanceof EmptyScreen)
			Minecraft.getInstance().displayGuiScreen(null);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
