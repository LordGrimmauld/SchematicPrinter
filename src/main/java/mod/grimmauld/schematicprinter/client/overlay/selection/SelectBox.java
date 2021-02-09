package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.tools.AbstractSelectTool;
import mod.grimmauld.schematicprinter.util.outline.AABBOutline;
import mod.grimmauld.schematicprinter.util.outline.Outline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SelectBox extends AbstractSelectTool {
	protected final SelectConfig<BlockPos> pos1;
	protected final SelectConfig<BlockPos> pos2;

	public SelectBox(ITextComponent description, int color, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, color);

		this.pos1 = pos1;
		this.pos2 = pos2;

		this.pos1.registerChangeListener(this::invalidateOutline);
		this.pos2.registerChangeListener(this::invalidateOutline);
	}

	public SelectBox(ITextComponent description, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		this(description, 0x6886c5, pos1, pos2);
	}

	@Nullable
	@Override
	protected Outline getUpdatedOutline() {
		if (outline != null)
			return outline;

		AxisAlignedBB bb = getBoundingBox();
		if (bb == null)
			return outline;

		return new AABBOutline(bb.grow(.01));
	}

	@Nullable
	protected AxisAlignedBB getBoundingBox() {
		BlockPos blockPos1 = pos1.getValue();
		BlockPos blockPos2 = pos2.getValue();

		if (blockPos1 == null || blockPos2 == null)
			return null;
		return new AxisAlignedBB(blockPos1, blockPos2).expand(1, 1, 1);
	}

	@Override
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
