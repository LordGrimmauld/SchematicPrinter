package mod.grimmauld.schematicprinter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.palette.PaletteOverlay;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteClearTool;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteEditTool;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteLoadConfig;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteSaveTool;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.client.schematics.SchematicHandler;
import mod.grimmauld.schematicprinter.client.schematics.select.*;
import mod.grimmauld.schematicprinter.client.schematics.tools.*;
import mod.grimmauld.sidebaroverlay.Manager;
import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.schematicprinter.util.TextHelper.translationComponent;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class SchematicPrinterClient {
	public static final SchematicHandler schematicHandler = new SchematicHandler();
	public static final PaletteOverlay paletteOverlay = new PaletteOverlay();

	public static BlockPosSelectConfig pos1;
	public static BlockPosSelectConfig pos2;

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		schematicHandler.tick();
	}

	@SubscribeEvent
	public static void onRenderWorld(RenderLevelLastEvent event) {
		PoseStack ms = event.getPoseStack();
		Camera info = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 view = info.getPosition();
		ms.pushPose();
		ms.translate(-view.x(), -view.y(), -view.z());
		SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
		schematicHandler.render(ms, buffer);
		buffer.draw();

		ms.popPose();
	}

	@SubscribeEvent
	public static void onRenderOverlay(RenderGameOverlayEvent.PostLayer event) {
		if (event.getOverlay() == ForgeIngameGui.HOTBAR_ELEMENT) {
			schematicHandler.renderOverlay(new PoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
		}
	}

	public static void setupOverlay(InterModEnqueueEvent event) {
		pos1 = new BlockPosSelectConfig(translationComponent("pos1"), ChatFormatting.YELLOW);
		pos2 = new BlockPosSelectConfig(translationComponent("pos2"), ChatFormatting.LIGHT_PURPLE);


		SelectOverlay schematicOverlay = new SelectOverlay(translationComponent("schematics"))
			.addOption(new SchematicSelectConfig(translationComponent("schematic.selected")))
			.addOption(new DeployTool(translationComponent("schematic.tool.deploy")))
			.addOption(new ClearSchematicSelectionTool(translationComponent("schematic.tool.clear")))
			.addOption(new FlipTool(translationComponent("schematic.tool.flip")))
			.addOption(new RotateTool(translationComponent("schematic.tool.rotate")))
			.addOption(new MoveTool(translationComponent("schematic.tool.move_xz")))
			.addOption(new MoveVerticalTool(translationComponent("schematic.tool.move_y")))
			.addOption(new InstantPrintTool(translationComponent("schematic.tool.print")))
			.register();


		SelectOverlay paletteEditOverlay = new SelectOverlay(translationComponent("palette_edit"))
			.addOption(new PaletteEditTool(translationComponent("palette.modify")))
			.addOption(new PaletteClearTool(translationComponent("palette.clear")))
			.addOption(new PaletteSaveTool(translationComponent("palette.save")))
			.addOption(new PaletteLoadConfig(translationComponent("palette.load")))
			.register();
		SelectItem paletteEditOverlayOpen = new SelectOpenOverlay(translationComponent("palette"), paletteEditOverlay).registerRenderHooks(paletteOverlay::render);

		SelectOverlay printerSettingsOverlay = new SelectOverlay(translationComponent("printer_settings_edit"))
			.addOption(new BooleanSelectConfig(translationComponent("replace_tes"), Printer.shouldReplaceTEs)
				.registerChangeListener(config -> Printer.shouldReplaceTEs = config.getValue()))
			.addOption(new BooleanSelectConfig(translationComponent("replace_blocks"), Printer.shouldReplaceBlocks)
				.registerChangeListener(config -> Printer.shouldReplaceBlocks = config.getValue()))
			.register();


		SelectOverlay boxTools = new SelectOverlay(translationComponent("tools.box"))
			.addOption(pos1)
			.addOption(pos2)
			.addOption(new SelectSchematicSave(translationComponent("schematic.save"), pos1, pos2))
			.addOption(paletteEditOverlayOpen)
			.addOption(new BoxBuildTool(translationComponent("tools.clear"), BuildToolStateSupplier.CLEAR, pos1, pos2))
			.addOption(new BoxBuildTool(translationComponent("tools.fill_palette"), BuildToolStateSupplier.FILL_FROM_PALETTE, pos1, pos2))
			.register();


		IntSelectConfig radius = new IntSelectConfig(translationComponent("circle.radius"), 0, 5, 100);
		IntSelectConfig height = new IntSelectConfig(translationComponent("circle.height"), 0, 1, 256);
		SelectOverlay circleTools = new SelectOverlay(translationComponent("tools.circle"))
			.addOption(pos1)
			.addOption(radius)
			.addOption(height)
			.addOption(paletteEditOverlayOpen)
			.addOption(new CircleBuildTool(translationComponent("tools.fill_palette"), pos1, radius, height, BuildToolStateSupplier.FILL_FROM_PALETTE))
			.addOption(new CircleBuildTool(translationComponent("tools.clear"), pos1, radius, height, BuildToolStateSupplier.CLEAR))
			.register();

		SelectOverlay sphereTools = new SelectOverlay(translationComponent("tools.sphere"))
			.addOption(pos1)
			.addOption(radius)
			.addOption(paletteEditOverlayOpen)
			.addOption(new SphereBuildTool(translationComponent("tools.fill_palette"), pos1, radius, BuildToolStateSupplier.FILL_FROM_PALETTE))
			.addOption(new SphereBuildTool(translationComponent("tools.clear"), pos1, radius, BuildToolStateSupplier.CLEAR))
			.register();


		SelectOverlay overlayMain = new SelectOverlay(TextHelper.translationComponent("overlay.main"))
			.addOption(new SelectOpenOverlay(translationComponent("schematics"), schematicOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("settings"), printerSettingsOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("box"), boxTools))
			.addOption(new SelectOpenOverlay(translationComponent("round"), circleTools))
			.addOption(new SelectOpenOverlay(translationComponent("sphere"), sphereTools))
			.register();
		InterModComms.sendTo(SidebarOverlay.MODID, Manager.IMC_ADD_OVERLAY_ENTRY, () -> new SelectOpenOverlay(translationComponent("building"), overlayMain));
	}

	@SubscribeEvent
	public static void onPlayerJoinWorld(ClientPlayerNetworkEvent.LoggedInEvent event) {
		schematicHandler.quitSchematic();
	}
}
