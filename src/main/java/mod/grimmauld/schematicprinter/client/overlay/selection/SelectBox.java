package mod.grimmauld.schematicprinter.client.overlay.selection;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SelectBox extends SelectItem {
	protected final BlockPosSelectConfig pos1;
	protected final BlockPosSelectConfig pos2;
	private final int color;
	@Nullable
	private AABBOutline outline = null;

	public SelectBox(ITextComponent description, int color, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description);
		this.color = color;
		this.pos1 = pos1;
		this.pos2 = pos2;

		this.pos1.registerChangeListener(config -> this.outline = null);
		this.pos2.registerChangeListener(config -> this.outline = null);
	}

	public SelectBox(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		this(new TranslationTextComponent(description), 0x6886c5, pos1, pos2);
	}

	@Override
	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		super.renderActive(ms, buffer);
		updateOutline();

		if (outline == null)
			return;
		outline.getParams()
			.colored(color)
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
	}

	private void updateOutline() {
		if (outline != null)
			return;

		AxisAlignedBB bb = getBoundingBox();
		if (bb == null)
			return;

		outline = new AABBOutline(bb.grow(.01));
	}

	@Nullable
	protected AxisAlignedBB getBoundingBox() {
		BlockPos blockPos1 = pos1.getPos();
		BlockPos blockPos2 = pos2.getPos();

		if (blockPos1 == null || blockPos2 == null)
			return null;
		return new AxisAlignedBB(blockPos1, blockPos2).expand(1, 1, 1);
	}

	protected Stream<BlockPos> getPositions() {
		AxisAlignedBB bb = getBoundingBox();
		if (bb == null)
			return Stream.empty();
		return IntStream.range(((int) bb.minX), ((int) bb.maxX)).boxed().flatMap(x ->
			IntStream.range(((int) bb.minZ), ((int) bb.maxZ)).boxed().flatMap(z ->
				IntStream.range(((int) bb.minY), ((int) bb.maxY)).mapToObj(y ->
					new BlockPos(x, y, z))));
	}
}
