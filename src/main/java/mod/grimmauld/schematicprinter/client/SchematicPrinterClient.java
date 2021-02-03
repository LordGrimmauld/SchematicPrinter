package mod.grimmauld.schematicprinter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectSchematicSave;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SchematicSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools.*;
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
		TOOL_DEACTIVATE = new KeyBinding(SchematicPrinter.MODID + ".keybind.menu", Keyboard.O.getKeycode(), SchematicPrinter.NAME);
		TOOL_SELECT = new KeyBinding(SchematicPrinter.MODID + ".keybind.select_tool", Keyboard.LALT.getKeycode(), SchematicPrinter.NAME);
		TOOL_ACTIVATE = new KeyBinding(SchematicPrinter.MODID + ".keybind.activate_tool", Keyboard.ENTER.getKeycode(), SchematicPrinter.NAME);
		TOOL_CONFIG = new KeyBinding(SchematicPrinter.MODID + ".keybind.config", Keyboard.CTRL.getKeycode(), SchematicPrinter.NAME);

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
		pos1 = new BlockPosSelectConfig("pos1", "pos1", TextFormatting.YELLOW);
		pos2 = new BlockPosSelectConfig("pos2", "pos2", TextFormatting.LIGHT_PURPLE);

		SelectOverlay schematicOverlay = new SelectOverlay("Schematics")
			.addOption(new SchematicSelectConfig("schematic", SchematicPrinter.MODID + ".schematic.selected"))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.deploy", new DeployTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.clear", new ClearTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.flip", new FlipTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.rotate", new RotateTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.move_xz", new MoveTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.move_y", new MoveVerticalTool()))
			.addOption(new SelectItem(SchematicPrinter.MODID + ".schematic.tool.print", new InstantPrintTool()))
			.register();

		SelectOverlay overlayMain = new SelectOverlay(SchematicPrinter.MODID + ".overlay.main")
			.configureDirectOpen(true)
			.addOption(pos1)
			.addOption(pos2)
			.addOption(new SelectSchematicSave(SchematicPrinter.MODID + ".schematic.save", pos1, pos2))
			.addOption(new BooleanSelectConfig("testbool1", "testBoolean", false))
			.addOption(new IntSelectConfig("testint1", "testInt", 0, 42, 100))
			.addOption(new SelectOpenOverlay(SchematicPrinter.MODID + ".schematics", schematicOverlay))
			.register();
	}
}
