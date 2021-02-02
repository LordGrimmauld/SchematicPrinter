package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.RaycastHelper;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPosSelectConfig extends SelectConfig {
	private static final Minecraft MC = Minecraft.getInstance();
	private final TextFormatting color;
	@Nullable
	private AABBOutline outline = null;
	@Nullable
	private BlockPos pos = null;

	public BlockPosSelectConfig(String key, ITextComponent description, TextFormatting color) {
		super(key, null, description);
		this.color = color;
	}

	public BlockPosSelectConfig(String key, String description) {
		this(key, new TranslationTextComponent(description), TextFormatting.AQUA);
	}

	public BlockPosSelectConfig(String key, String description, TextFormatting color) {
		this(key, new TranslationTextComponent(description), color);
	}

	@Override
	public void onScrolled(int amount) {

	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null || MC.world == null)
			return;

		BlockRayTraceResult trace = RaycastHelper.rayTraceRange(MC.world, MC.player, 75);
		if (trace.getType() != RayTraceResult.Type.BLOCK)
			return;

		BlockPos hit = new BlockPos(trace.getHitVec());
		if (MC.world.getBlockState(hit).getMaterial().isReplaceable())
			hit = hit.offset(trace.getFace().getOpposite());
		pos = hit;
		outline = null;
		onValueChanged();
	}

	@Override
	protected ITextComponent getState() {
		return new StringTextComponent(pos == null ? "undefined" : pos.getX() + " " + pos.getY() + " " + pos.getZ()).applyTextStyle(color);
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
