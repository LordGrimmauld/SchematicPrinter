package mod.grimmauld.schematicprinter.client.palette.select;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.RaycastHelper;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.sidebaroverlay.Manager.TOOL_CONFIG;

@ParametersAreNonnullByDefault
public class PaletteEditTool extends SelectItem {

	public PaletteEditTool(Component description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);

		BlockPos hit = RaycastHelper.getFocusedPosition();
		if (hit == null || MC.player == null || MC.level == null)
			return;

		boolean sneaking = MC.player.isShiftKeyDown();
		BlockState state = MC.level.getBlockState(hit);

		if (sneaking && PaletteManager.containsState(state))
			PaletteManager.removeFromPalette(state);
		else if (sneaking)
			PaletteManager.addToPalette(state);
		else if (PaletteManager.containsBlock(state.getBlock()))
			PaletteManager.removeFromPalette(state.getBlock());
		else
			PaletteManager.addToPalette(state.getBlock().defaultBlockState());
	}


	@Override
	public void onScroll(InputEvent.MouseScrollEvent event) {
		super.onScroll(event);

		if (TOOL_CONFIG.isDown()) {
			this.modifyPalette((int) Math.signum(event.getScrollDelta()));
			event.setCanceled(true);
		}
	}

	private void modifyPalette(int amount) {
		if (amount == 0)
			return;

		BlockPos hit = RaycastHelper.getFocusedPosition();
		if (hit == null || MC.player == null || MC.level == null)
			return;

		boolean sneaking = MC.player.isShiftKeyDown();
		BlockState state = MC.level.getBlockState(hit);

		if (amount > 0) {
			PaletteManager.increaseWeight(sneaking ? state : state.getBlock().defaultBlockState(), amount);
			return;
		}
		if (sneaking) {
			PaletteManager.decreaseWeight(state, amount);
			return;
		}
		PaletteManager.decreaseWeight(state.getBlock(), amount);
	}

	@Override
	public void renderActive(PoseStack ms, SuperRenderTypeBuffer buffer) {
		super.renderActive(ms, buffer);

		BlockPos pos = RaycastHelper.getFocusedPosition();
		if (pos == null)
			return;

		Outline outline = new AABBOutline(new AABB(pos));
		outline.getParams()
			.colored(11141290)
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);
		SchematicPrinterClient.paletteOverlay.render(event);
	}
}