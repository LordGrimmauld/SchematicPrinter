package mod.grimmauld.schematicprinter.client.printer;

import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.util.LazyQueue;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
@SuppressWarnings("unused")
public class Printer {
	private static final Minecraft MC = Minecraft.getInstance();
	private static final LazyQueue<BlockInformation> printQueue = new LazyQueue<>();
	public static boolean shouldReplaceTEs = true;
	public static boolean shouldReplaceBlocks = true;
	private static boolean shouldPrint = false;
	private static boolean receivedEndFeedback = true;

	@SubscribeEvent
	public static void tick(TickEvent.ClientTickEvent event) {
		if (shouldPrint)
			print();
	}

	private static void print() {
		if (MC.level == null || MC.player == null)
			return;

		printQueue.runForN(inf -> MC.player.chat(inf.getPrintCommand()), 512, Printer::canPlace);

		if (printQueue.isEmpty())
			stopPrinting();
	}

	private static boolean canPlace(BlockInformation inf) {
		if (MC.level == null || MC.player == null)
			return false;
		if (MC.level.isOutsideBuildHeight(inf.pos))
			return false;
		// if (MC.world.getWorldInfo().getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES)
		// 	return false;
		BlockState replaceState = MC.level.getBlockState(inf.pos);
		if (replaceState.equals(inf.state))
			return false;
		if (!MC.level.isUnobstructed(inf.state, inf.pos, CollisionContext.of(MC.player)))
			return false;
		if (replaceState.isAir())
			return true;
		if (!shouldReplaceTEs && replaceState.hasBlockEntity())
			return false;
		if (inf.state.getBlock() == Blocks.AIR)
			return inf.overrideAir;
		return shouldReplaceBlocks;
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onCommandFeedback(ClientChatReceivedEvent event) {
		if (event.getMessage() == null || MC.player == null)
			return;
		List<Component> checking = new LinkedList<>();
		checking.add(event.getMessage());

		while (!checking.isEmpty()) {
			Component iTextComponent = checking.get(0);
			if (iTextComponent instanceof TranslatableComponent) {
				String test = ((TranslatableComponent) iTextComponent).getKey();
				if (test.equals("command.unknown.command")) {
					stopPrinting();
					event.setMessage(new TextComponent(
						ChatFormatting.RED + "You do not have permission to print on this server."));
					return;
				}
				if (test.equals("parsing.int.expected")) {
					MC.player
						.chat("/me is printing a structure with " + SchematicPrinter.NAME);
					MC.player.chat("/gamerule sendCommandFeedback false");
					MC.player.chat("/gamerule logAdminCommands false");
					event.setCanceled(true);
					return;
				}
				Object[] args = ((TranslatableComponent) iTextComponent).getArgs();
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
		if (MC.level == null)
			return Stream.empty();
		return test.filter(inf -> MC.level.getBlockState(inf.pos) != inf.state);
	}

	@SubscribeEvent
	public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
		printQueue.clear();
		shouldPrint = false;
		receivedEndFeedback = true;
	}

	public static void startPrinting() {
		receivedEndFeedback = true;
		if (printQueue.isEmpty())
			return;
		shouldPrint = true;
		if (MC.player == null)
			return;
		MC.player.displayClientMessage(new TextComponent("Printing Structure..."), true);
		MC.player.chat("/gamerule sendCommandFeedback false");
		MC.player.chat("/gamerule logAdminCommands false");
	}

	public static void stopPrinting() {
		shouldPrint = false;
		printQueue.clear();
		if (MC.player == null)
			return;
		MC.player.displayClientMessage(new TextComponent("Printing done"), true);
		MC.player.chat("/gamerule logAdminCommands true");
		MC.player.chat("/gamerule sendCommandFeedback true");
		receivedEndFeedback = false;
	}

	public static void addAll(Stream<BlockInformation> blocks) {
		if (MC.level == null)
			return;
		printQueue.addAll(blocks);
	}
}
