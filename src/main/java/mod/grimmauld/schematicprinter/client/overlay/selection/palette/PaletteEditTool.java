package mod.grimmauld.schematicprinter.client.overlay.selection.palette;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.RaycastHelper;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import mod.grimmauld.schematicprinter.util.outline.Outline;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PaletteEditTool extends SelectItem {

	public PaletteEditTool(ITextComponent description) {
		super(description);
	}

	public PaletteEditTool(String description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);

		BlockPos hit = RaycastHelper.getFocusedPosition();
		if (hit == null || MC.player == null || MC.world == null)
			return;

		boolean sneaking = MC.player.isSneaking();
		BlockState state = MC.world.getBlockState(hit);

		if (sneaking && PaletteManager.containsState(state))
			PaletteManager.removeFromPalette(state);
		else if (sneaking)
			PaletteManager.addToPalette(state);
		else if (PaletteManager.containsBlock(state.getBlock()))
			PaletteManager.removeFromPalette(state.getBlock());
		else
			PaletteManager.addToPalette(state.getBlock().getDefaultState());
	}


	@Override
	public void onScroll(InputEvent.MouseScrollEvent event) {
		super.onScroll(event);

		if (SchematicPrinterClient.TOOL_CONFIG.isKeyDown()) {
			this.modifyPalette((int) Math.signum(event.getScrollDelta()));
			event.setCanceled(true);
		}
	}

	private void modifyPalette(int amount) {
		if (amount == 0)
			return;

		BlockPos hit = RaycastHelper.getFocusedPosition();
		if (hit == null || MC.player == null || MC.world == null)
			return;

		boolean sneaking = MC.player.isSneaking();
		BlockState state = MC.world.getBlockState(hit);

		if (amount > 0) {
			PaletteManager.increaseWeight(sneaking ? state : state.getBlock().getDefaultState(), amount);
			return;
		}
		if (sneaking) {
			PaletteManager.decreaseWeight(state, amount);
			return;
		}
		PaletteManager.decreaseWeight(state.getBlock(), amount);
	}

	@Override
	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		super.renderActive(ms, buffer);

		BlockPos pos = RaycastHelper.getFocusedPosition();
		if (pos == null)
			return;

		Outline outline = new AABBOutline(new AxisAlignedBB(pos));
		outline.getParams()
			.colored(11141290)
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
	}

	@Override
	public boolean shouldRenderPalette() {
		return true;
	}
}
