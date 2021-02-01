package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
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
import net.minecraft.util.math.Vec3d;

public abstract class SchematicToolBase implements ISchematicTool {
	protected SchematicHandler schematicHandler;
	protected BlockPos selectedPos;
	protected Vec3d chasingSelectedPos;
	protected Vec3d lastChasingSelectedPos;
	protected boolean selectIgnoreBlocks;
	protected int selectionRange;
	protected boolean schematicSelected;
	protected boolean renderSelectedFace;
	protected Direction selectedFace;

	@Override
	public void init() {
		schematicHandler = SchematicPrinterClient.schematicHandler;
		selectedPos = null;
		selectedFace = null;
		schematicSelected = false;
		chasingSelectedPos = Vec3d.ZERO;
		lastChasingSelectedPos = Vec3d.ZERO;
	}

	@Override
	public void updateSelection() {
		updateTargetPos();

		if (selectedPos == null)
			return;
		lastChasingSelectedPos = chasingSelectedPos;
		Vec3d target = new Vec3d(selectedPos);
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

			Vec3d traceOrigin = RaycastHelper.getTraceOrigin(player);
			Vec3d start = inf.transformation.toLocalSpace(traceOrigin);
			Vec3d end = inf.transformation.toLocalSpace(RaycastHelper.getTraceTarget(player, 70, traceOrigin));
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
				lastChasingSelectedPos = chasingSelectedPos = new Vec3d(selectedPos);
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
			lastChasingSelectedPos = chasingSelectedPos = new Vec3d(selectedPos);
	}

	@Override
	public void renderTool(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	@Override
	public void renderOverlay(MatrixStack ms, IRenderTypeBuffer buffer) {
	}

	@Override
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
	public boolean handleActivated() {
		return false;
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		return false;
	}
}
