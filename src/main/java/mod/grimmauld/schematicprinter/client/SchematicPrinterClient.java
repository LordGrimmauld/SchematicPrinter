package mod.grimmauld.schematicprinter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SchematicSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.palette.PaletteClearTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.palette.PaletteEditTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.palette.PaletteLoadConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.palette.PaletteSaveTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools.*;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.BoxBuildTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.BuildToolStateSupplier;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.CircleBuildTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.SphereBuildTool;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.client.schematics.SchematicHandler;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.schematicprinter.util.TextHelper.translationComponent;
import static mod.grimmauld.schematicprinter.util.TextHelper.translationKey;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class SchematicPrinterClient {
	public static final SchematicHandler schematicHandler = new SchematicHandler();

	public static KeyBinding TOOL_CONFIG;
	public static KeyBinding TOOL_DEACTIVATE;
	public static KeyBinding TOOL_SELECT;
	public static KeyBinding TOOL_ACTIVATE;

	public static BlockPosSelectConfig pos1;
	public static BlockPosSelectConfig pos2;

	public static void init() {
		TOOL_DEACTIVATE = new KeyBinding(translationKey("keybind.menu"), Keyboard.O.getKeycode(), SchematicPrinter.NAME);
		TOOL_SELECT = new KeyBinding(translationKey("keybind.select_tool"), Keyboard.LALT.getKeycode(), SchematicPrinter.NAME);
		TOOL_ACTIVATE = new KeyBinding(translationKey("keybind.activate_tool"), Keyboard.ENTER.getKeycode(), SchematicPrinter.NAME);
		TOOL_CONFIG = new KeyBinding(translationKey("keybind.config"), Keyboard.CTRL.getKeycode(), SchematicPrinter.NAME);

		ClientRegistry.registerKeyBinding(TOOL_DEACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_SELECT);
		ClientRegistry.registerKeyBinding(TOOL_ACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_CONFIG);

		setupOverlay();
	}

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
			onRenderHotbar(new MatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
		}
	}

	public static void onRenderHotbar(MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		schematicHandler.renderOverlay(ms, buffer);
	}

	private static void setupOverlay() {
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
		SelectItem paletteEditOverlayOpen = new SelectOpenOverlay(translationComponent("palette"), paletteEditOverlay).shouldRenderPalette(true);

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


		SelectOverlay overlayMain = new SelectOverlay(translationComponent("overlay.main"))
			.configureDirectOpen(true)
			.addOption(new SelectOpenOverlay(translationComponent("schematics"), schematicOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("settings"), printerSettingsOverlay))
			.addOption(new SelectOpenOverlay(translationComponent("box"), boxTools))
			.addOption(new SelectOpenOverlay(translationComponent("round"), circleTools))
			.addOption(new SelectOpenOverlay(translationComponent("sphere"), sphereTools))
			.register();
	}
}
