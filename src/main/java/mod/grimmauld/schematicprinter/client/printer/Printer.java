package mod.grimmauld.schematicprinter.client.printer;

import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Stream;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
@SuppressWarnings("unused")
public class Printer {
	private static final Minecraft MC = Minecraft.getInstance();
	private static final Queue<BlockInformation> printQueue = new PriorityQueue<>();
	private static boolean shouldPrint = false;
	private static boolean receivedEndFeedback = true;

	@SubscribeEvent
	public static void tick(TickEvent.ClientTickEvent event) {
		if (shouldPrint)
			print();
	}

	private static void print() {
		if (MC.world == null || MC.player == null)
			return;

		for (int i = 0; i < 500; i++) { // todo: Add config option
			BlockInformation inf = printQueue.poll();
			if (inf == null)
				break;
			if (!World.isOutsideBuildHeight(inf.pos) && MC.world.func_226663_a_(inf.state, inf.pos, ISelectionContext.forEntity(MC.player)))
				MC.player.sendChatMessage(inf.getPrintCommand());

		}
		if (printQueue.isEmpty())
			stopPrinting();
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
					stopPrinting();
					event.setMessage(new StringTextComponent(
						TextFormatting.RED + "You do not have permission to print on this server."));
					return;
				}
				if (test.equals("parsing.int.expected")) {
					MC.player
						.sendChatMessage("/me is printing a structure with " + SchematicPrinter.NAME);
					MC.player.sendChatMessage("/gamerule sendCommandFeedback false");
					MC.player.sendChatMessage("/gamerule logAdminCommands false");
					event.setCanceled(true);
					return;
				}
				Object[] args = ((TranslationTextComponent) iTextComponent).getFormatArgs();
				if (!receivedEndFeedback && test.equals("commands.gamerule.set") && args.length == 2
					&& args[0].equals("sendCommandFeedback") && args[1].equals("true")) {
					receivedEndFeedback = true;
					event.setCanceled(true);
					return;
				}
			} else {
				checking.addAll(iTextComponent.getSiblings());
			}
			checking.remove(iTextComponent);
		}
	}

	private static Stream<BlockInformation> getFilteredOf(Stream<BlockInformation> test) {
		if (MC.world == null)
			return Stream.empty();
		return test.filter(inf -> MC.world.getBlockState(inf.pos) != inf.state);
	}

	@SubscribeEvent
	public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
		printQueue.clear();
		shouldPrint = false;
		receivedEndFeedback = true;
	}

	public static void startPrinting() {
		receivedEndFeedback = true;
		if (printQueue.peek() == null)
			return;
		shouldPrint = true;
		if (MC.player == null)
			return;
		MC.player.sendStatusMessage(new StringTextComponent("Printing Structure..."), true);
		MC.player.sendChatMessage("/gamerule sendCommandFeedback false");
		MC.player.sendChatMessage("/gamerule logAdminCommands false");
	}

	public static void stopPrinting() {
		shouldPrint = false;
		printQueue.clear();
		if (MC.player == null)
			return;
		MC.player.sendStatusMessage(new StringTextComponent("Printing done"), true);
		MC.player.sendChatMessage("/gamerule logAdminCommands true");
		MC.player.sendChatMessage("/gamerule sendCommandFeedback true");
		receivedEndFeedback = false;
	}

	public static void add(BlockInformation blockInformation) {
		if (MC.world != null && MC.world.getBlockState(blockInformation.pos) != blockInformation.state)
			printQueue.add(blockInformation);
	}

	public static void addAll(Stream<BlockInformation> blocks) {
		if (MC.world == null)
			return;
		blocks.filter(inf -> MC.world.getBlockState(inf.pos) != inf.state).forEach(printQueue::add);
	}
}
