package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.schematics.SchematicHandler;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.RaycastHelper;
import mod.grimmauld.schematicprinter.util.VecHelper;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SchematicToolBase extends SelectItem {
	protected SchematicHandler schematicHandler;
	protected BlockPos selectedPos;
	protected Vector3d chasingSelectedPos;
	protected Vector3d lastChasingSelectedPos;
	protected boolean selectIgnoreBlocks;
	protected int selectionRange;
	protected boolean schematicSelected;
	protected boolean renderSelectedFace;
	protected Direction selectedFace;

	public SchematicToolBase(ITextComponent description) {
		super(description);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		schematicHandler = SchematicPrinterClient.schematicHandler;
		selectedPos = null;
		selectedFace = null;
		schematicSelected = false;
		chasingSelectedPos = Vector3d.ZERO;
		lastChasingSelectedPos = Vector3d.ZERO;
	}

	public void updateSelection() {
		updateTargetPos();

		if (selectedPos == null)
			return;
		lastChasingSelectedPos = chasingSelectedPos;
		Vector3d target = Vector3d.copy(selectedPos);
		if (target.distanceTo(chasingSelectedPos) < 1 / 512f) {
			chasingSelectedPos = target;
			return;
		}

		chasingSelectedPos = chasingSelectedPos.add(target.subtract(chasingSelectedPos)
			.scale(1 / 2f));
	}

	public void updateTargetPos() {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		SchematicMetaInf inf = schematicHandler.activeSchematic;

		if (player == null || inf == null)
			return;

		// Select Blueprint
		if (schematicHandler.isDeployed()) {

			Vector3d traceOrigin = RaycastHelper.getTraceOrigin(player);
			Vector3d start = inf.transformation.toLocalSpace(traceOrigin);
			Vector3d end = inf.transformation.toLocalSpace(RaycastHelper.getTraceTarget(player, 70, traceOrigin));
			RaycastHelper.PredicateTraceResult result =
				RaycastHelper.rayTraceUntil(start, end, pos -> inf.bounds.contains(VecHelper.getCenterOf(pos)));

			schematicSelected = result != null && !result.missed();
			selectedFace = schematicSelected ? result.getFacing() : null;
		}

		boolean snap = this.selectedPos == null;

		// Select location at distance
		if (selectIgnoreBlocks) {
			float pt = Minecraft.getInstance()
				.getRenderPartialTicks();
			selectedPos = new BlockPos(player.getEyePosition(pt)
				.add(player.getLookVec()
					.scale(selectionRange)));
			if (snap)
				lastChasingSelectedPos = chasingSelectedPos = Vector3d.copy(selectedPos);
			return;
		}

		// Select targeted Block
		selectedPos = null;
		BlockRayTraceResult trace = RaycastHelper.rayTraceRange(player.world, player, 75);
		if (trace.getType() != RayTraceResult.Type.BLOCK)
			return;

		BlockPos hit = new BlockPos(trace.getHitVec());
		boolean replaceable = player.world.getBlockState(hit)
			.getMaterial()
			.isReplaceable();
		if (trace.getFace()
			.getAxis()
			.isVertical() && !replaceable)
			hit = hit.offset(trace.getFace());
		selectedPos = hit;
		if (snap)
			lastChasingSelectedPos = chasingSelectedPos = Vector3d.copy(selectedPos);
	}

	public void renderTool(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	public void renderOverlay(MatrixStack ms, IRenderTypeBuffer buffer) {
	}

	public void renderOnSchematic(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		if (schematicHandler == null)
			schematicHandler = SchematicPrinterClient.schematicHandler;
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (!schematicHandler.isDeployed() || inf == null)
			return;
		ms.push();
		AABBOutline outline = inf.outline;
		if (renderSelectedFace) {
			outline.getParams()
				.highlightFace(selectedFace)
				.withFaceTextures(ExtraTextures.CHECKERED,
					SchematicPrinterClient.TOOL_CONFIG.isKeyDown() ? ExtraTextures.HIGHLIGHT_CHECKERED : ExtraTextures.CHECKERED);
		}
		outline.getParams()
			.colored(0x6886c5)
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
		ms.pop();
	}

	@Override
	public void onScroll(InputEvent.MouseScrollEvent event) {
		super.onScroll(event);
		if (SchematicPrinterClient.TOOL_CONFIG.isKeyDown())
			event.setCanceled(handleMouseWheel(event.getScrollDelta()));
	}

	public boolean handleMouseWheel(double delta) {
		return false;
	}
}
