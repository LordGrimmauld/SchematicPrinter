package mod.grimmauld.schematicprinter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.api.RegisterOverlayEvent;
import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.config.SchematicSelectConfig;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.tools.BoxBuildTool;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.tools.BuildToolStateSupplier;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.tools.CircleBuildTool;
import mod.grimmauld.schematicprinter.client.api.overlay.selection.tools.SphereBuildTool;
import mod.grimmauld.schematicprinter.client.palette.PaletteOverlay;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteClearTool;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteEditTool;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteLoadConfig;
import mod.grimmauld.schematicprinter.client.palette.select.PaletteSaveTool;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.client.schematics.SchematicHandler;
import mod.grimmauld.schematicprinter.client.schematics.select.*;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	public static void onRenderWorld(RenderWorldLastEvent event) {
		MatrixStack ms = event.getMatrixStack();
		ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
		Vector3d view = info.getProjectedView();
		ms.push();
		ms.translate(-view.getX(), -view.getY(), -view.getZ());
		SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
		schematicHandler.render(ms, buffer);
		Manager.getActiveOverlay().ifPresent(overlay -> overlay.options.forEach(selectItem -> selectItem.continuousRendering(ms, buffer)));
		Manager.getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.renderActive(ms, buffer));
		buffer.draw();

		ms.pop();
	}

	@SubscribeEvent
	public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			schematicHandler.renderOverlay(new MatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getBufferSource());
		}
	}

	public static void setupOverlay(RegisterOverlayEvent event) {
		pos1 = new BlockPosSelectConfig(translationComponent("pos1"), TextFormatting.YELLOW);
		pos2 = new BlockPosSelectConfig(translationComponent("pos2"), TextFormatting.LIGHT_PURPLE);


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


		event.overlayMain
			.addOption(new SelectOpenOverlay(translationComponent("schematics"), schematicOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("settings"), printerSettingsOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("box"), boxTools))
			.addOption(new SelectOpenOverlay(translationComponent("round"), circleTools))
			.addOption(new SelectOpenOverlay(translationComponent("sphere"), sphereTools));
	}
}
