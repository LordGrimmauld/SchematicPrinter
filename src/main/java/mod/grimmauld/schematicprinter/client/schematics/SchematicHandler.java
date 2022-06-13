package mod.grimmauld.schematicprinter.client.schematics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.client.schematics.select.EmptySchematicTool;
import mod.grimmauld.schematicprinter.client.schematics.select.SchematicToolBase;
import mod.grimmauld.sidebaroverlay.Manager;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Vec3i;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Vector;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SchematicHandler {
	private final Vector<SchematicRenderer> renderers = new Vector<>(3);
	private final LazyLoadedValue<PlacementDetectWorld> placementWorld = new LazyLoadedValue<>(() -> new PlacementDetectWorld(Minecraft.getInstance().level));
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

		Vec3i size = activeSchematic.structure.getSize();
		Level clientWorld = Minecraft.getInstance().level;
		if (!size.equals(BlockPos.ZERO) && clientWorld != null) {
			SchematicWorld w = new SchematicWorld(clientWorld);
			SchematicWorld wMirroredFB = new SchematicWorld(clientWorld);
			SchematicWorld wMirroredLR = new SchematicWorld(clientWorld);
			StructurePlaceSettings placementSettings = new StructurePlaceSettings();
			BlockPos pos = BlockPos.ZERO;
			activeSchematic.structure.placeInWorld(w, pos, pos, placementSettings, w.getRandom(), Block.UPDATE_CLIENTS);

			placementSettings.setMirror(Mirror.FRONT_BACK);
			pos = BlockPos.ZERO.east(size.getX() - 1);
			activeSchematic.structure.placeInWorld(wMirroredFB, pos, pos, placementSettings, wMirroredFB.getRandom(), Block.UPDATE_CLIENTS);

			placementSettings.setMirror(Mirror.LEFT_RIGHT);
			pos = BlockPos.ZERO.south(size.getZ() - 1);
			activeSchematic.structure.placeInWorld(wMirroredLR, pos, pos, placementSettings, wMirroredLR.getRandom(), Block.UPDATE_CLIENTS);
			this.renderers.get(0).display(w);
			this.renderers.get(1).display(wMirroredFB);
			this.renderers.get(2).display(wMirroredLR);
		}
	}

	public void render(PoseStack ms, SuperRenderTypeBuffer buffer) {
		ms.pushPose();
		getActiveTool().ifPresent(tool -> tool.renderTool(ms, buffer));
		ms.popPose();

		if (this.active && activeSchematic != null) {
			ms.pushPose();
			activeSchematic.transformation.applyGLTransformations(ms);
			if (!this.renderers.isEmpty()) {
				float pt = Minecraft.getInstance().getFrameTime();
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
			ms.popPose();
		}
	}

	public void renderOverlay(PoseStack ms, MultiBufferSource buffer) {
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

		PlacementDetectWorld world = placementWorld.get();
		world.clearBuffer();
		activeSchematic.structure.placeInWorld(world, activeSchematic.transformation.getAnchor(), activeSchematic.transformation.getAnchor(), activeSchematic.transformation.toSettings(), world.getRandom(), Block.UPDATE_CLIENTS);
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
		return Manager.getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).filter(SchematicToolBase.class::isInstance).map(SchematicToolBase.class::cast);
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
