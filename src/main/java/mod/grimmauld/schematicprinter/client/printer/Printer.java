package mod.grimmauld.schematicprinter.client.printer;

import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
@SuppressWarnings("unused")
public class Printer {
	private static final Minecraft MC = Minecraft.getInstance();
	public static List<BlockInformation> printQueue = new ArrayList<>();
	static boolean approved;

	@SubscribeEvent
	public static void tick(TickEvent.ClientTickEvent event) {
		if (MC.world == null || MC.player == null)
			return;

		for (int i = 0; i < 10; i++) {
			if (printQueue.isEmpty())
				return;
			BlockInformation inf = printQueue.get(0);
			printQueue.remove(0);

			if (MC.world.getBlockState(inf.pos) == inf.state)
				continue;

			// canPlace
			if (!MC.world.func_226663_a_(inf.state, inf.pos, ISelectionContext.forEntity(MC.player)))
				continue;

			MC.player.sendChatMessage(inf.getPrintCommand());
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onCommandFeedback(ClientChatReceivedEvent event) {
		if (event.getMessage() == null || MC.player == null)
			return;
		List<ITextComponent> checking = new LinkedList<>();
		checking.add(event.getMessage());

		while (!checking.isEmpty()) {
			ITextComponent iTextComponent = checking.get(0);
			if (iTextComponent instanceof TranslationTextComponent) {
				String test = ((TranslationTextComponent) iTextComponent).getKey();
				if (test.equals("command.unknown.command")) {
					printQueue.clear();
					event.setMessage(new StringTextComponent(
						TextFormatting.RED + "You do not have permission to print on this server."));
					return;
				}
				if (test.equals("parsing.int.expected")) {
					approved = true;
					MC.player
						.sendChatMessage("/me is printing a structure with " + SchematicPrinter.NAME);
					MC.player.sendChatMessage("/gamerule sendCommandFeedback false");
					MC.player.sendChatMessage("/gamerule logAdminCommands false");
					event.setCanceled(true);
					return;
				}
			} else {
				checking.addAll(iTextComponent.getSiblings());
			}
			checking.remove(iTextComponent);
		}
	}

	@SubscribeEvent
	public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
		printQueue.clear();
	}
}
