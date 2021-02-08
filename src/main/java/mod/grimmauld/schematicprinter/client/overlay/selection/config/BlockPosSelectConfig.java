package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.RaycastHelper;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPosSelectConfig extends SelectConfig {
	private final TextFormatting color;
	@Nullable
	private AABBOutline outline = null;
	@Nullable
	private BlockPos pos = null;

	public BlockPosSelectConfig(ITextComponent description, TextFormatting color) {
		super(description);
		this.color = color;
	}

	@Override
	public void onScrolled(int amount) {

	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		BlockPos hit = RaycastHelper.getFocusedPosition();
		if (hit == null)
			return;
		pos = hit;
		outline = null;
		onValueChanged();
	}

	@Override
	protected ITextComponent getState() {
		return new StringTextComponent(pos == null ? "undefined" : pos.getX() + " " + pos.getY() + " " + pos.getZ()).mergeStyle(color);
	}

	@Override
	public void continuousRendering(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		super.continuousRendering(ms, buffer);
		if (pos == null)
			return;
		if (outline == null)
			outline = new AABBOutline(new AxisAlignedBB(pos));
		outline.getParams()
			.colored(color.getColor() == null ? 11141290 : color.getColor())
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
	}

	@Nullable
	public BlockPos getPos() {
		return pos;
	}
}
