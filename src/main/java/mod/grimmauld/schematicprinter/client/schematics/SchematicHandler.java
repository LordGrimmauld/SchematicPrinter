package mod.grimmauld.schematicprinter.client.schematics;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.Manager;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools.EmptySchematicTool;
import mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools.SchematicToolBase;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.PlacementSettings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Vector;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SchematicHandler {
	private final Vector<SchematicRenderer> renderers = new Vector<>(3);
	private final LazyValue<PlacementDetectWorld> placementWorld = new LazyValue<>(() -> new PlacementDetectWorld(Minecraft.getInstance().world));
	@Nullable
	public SchematicMetaInf activeSchematic;
	private boolean deployed;
	private boolean active;

	public SchematicHandler() {
		for (int i = 0; i < this.renderers.capacity(); ++i) {
			this.renderers.add(new SchematicRenderer());
		}
	}

	public void quitSchematic() {
		deployed = false;
		this.renderers.forEach(r -> r.setActive(false));
	}

	public void tick() {
		if (activeSchematic != null)
			activeSchematic.transformation.tick();
		if (active)
			renderers.forEach(SchematicRenderer::tick);
		getActiveTool().ifPresent(SchematicToolBase::updateSelection);
	}

	private void setupRenderer() {
		if (activeSchematic == null)
			return;

		BlockPos size = activeSchematic.structure.getSize();
		World clientWorld = Minecraft.getInstance().world;
		if (!size.equals(BlockPos.ZERO) && clientWorld != null) {
			SchematicWorld w = new SchematicWorld(clientWorld);
			SchematicWorld wMirroredFB = new SchematicWorld(clientWorld);
			SchematicWorld wMirroredLR = new SchematicWorld(clientWorld);
			PlacementSettings placementSettings = new PlacementSettings();
			activeSchematic.structure.addBlocksToWorld(w, BlockPos.ZERO, placementSettings);
			placementSettings.setMirror(Mirror.FRONT_BACK);
			activeSchematic.structure.addBlocksToWorld(wMirroredFB, BlockPos.ZERO.east(size.getX() - 1), placementSettings);
			placementSettings.setMirror(Mirror.LEFT_RIGHT);
			activeSchematic.structure.addBlocksToWorld(wMirroredLR, BlockPos.ZERO.south(size.getZ() - 1), placementSettings);
			this.renderers.get(0).display(w);
			this.renderers.get(1).display(wMirroredFB);
			this.renderers.get(2).display(wMirroredLR);
		}
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		ms.push();
		getActiveTool().ifPresent(tool -> tool.renderTool(ms, buffer));
		ms.pop();

		if (this.active && activeSchematic != null) {
			ms.push();
			activeSchematic.transformation.applyGLTransformations(ms);
			if (!this.renderers.isEmpty()) {
				float pt = Minecraft.getInstance().getRenderPartialTicks();
				boolean lr = activeSchematic.transformation.getScaleLR().get(pt) < 0.0F;
				boolean fb = activeSchematic.transformation.getScaleFB().get(pt) < 0.0F;
				if (lr && !fb) {
					this.renderers.get(2).render(ms, buffer);
				} else if (fb && !lr) {
					this.renderers.get(1).render(ms, buffer);
				} else {
					this.renderers.get(0).render(ms, buffer);
				}
			}

			getActiveTool().orElse(EmptySchematicTool.INSTANCE).renderOnSchematic(ms, buffer);
			ms.pop();
		}
	}

	public void renderOverlay(MatrixStack ms, IRenderTypeBuffer buffer) {
		if (this.active) {
			getActiveTool().ifPresent(tool -> tool.renderOverlay(ms, buffer));
		}
	}

	public void deploy() {
		this.deployed = true;
		this.setupRenderer();
	}

	public void printInstantly() {
		if (this.activeSchematic == null || !deployed)
			return;

		PlacementDetectWorld world = placementWorld.getValue();
		world.clearBuffer();
		activeSchematic.structure.addBlocksToWorld(world, activeSchematic.transformation.getAnchor(), activeSchematic.transformation.toSettings());
		world.printBuffer();
		quitSchematic();
		Printer.startPrinting();
	}

	public boolean isActive() {
		return this.active;
	}

	public boolean isDeployed() {
		return this.deployed;
	}

	private Optional<SchematicToolBase> getActiveTool() {
		return Manager.getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).filter(selectItem -> selectItem instanceof SchematicToolBase).map(selectItem -> (SchematicToolBase) selectItem);
	}

	public void setActiveSchematic(@Nullable String selectedFile) {
		if (selectedFile == null) {
			activeSchematic = null;
			active = false;
			deployed = false;
			this.renderers.forEach(r -> r.setActive(false));
			return;
		}
		activeSchematic = SchematicMetaInf.load(selectedFile);
		active = true;
	}
}
