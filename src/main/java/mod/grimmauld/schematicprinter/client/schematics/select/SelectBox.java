package mod.grimmauld.schematicprinter.client.schematics.select;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.tools.AbstractSelectTool;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SelectBox extends AbstractSelectTool {
	protected final SelectConfig<BlockPos> pos1;
	protected final SelectConfig<BlockPos> pos2;

	public SelectBox(Component description, int color, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, color);

		this.pos1 = pos1;
		this.pos2 = pos2;

		this.pos1.registerChangeListener(this::invalidateOutline);
		this.pos2.registerChangeListener(this::invalidateOutline);
	}

	public SelectBox(Component description, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		this(description, 0x6886c5, pos1, pos2);
	}

	@Nullable
	@Override
	protected Outline getUpdatedOutline() {
		if (outline != null)
			return outline;

		AABB bb = getBoundingBox();
		if (bb == null)
			return outline;

		return new AABBOutline(bb.inflate(.01));
	}

	@Nullable
	protected AABB getBoundingBox() {
		BlockPos blockPos1 = pos1.getValue();
		BlockPos blockPos2 = pos2.getValue();

		if (blockPos1 == null || blockPos2 == null)
			return null;
		return new AABB(blockPos1, blockPos2).expandTowards(1, 1, 1);
	}

	@Override
	protected Stream<BlockPos> getPositions() {
		AABB bb = getBoundingBox();
		if (bb == null)
			return Stream.empty();
		return IntStream.range(((int) bb.minY), ((int) bb.maxY)).boxed().flatMap(y ->
			IntStream.range(((int) bb.minX), ((int) bb.maxX)).boxed().flatMap(x ->
				IntStream.range(((int) bb.minZ), ((int) bb.maxZ)).mapToObj(z ->
					new BlockPos(x, y, z))));
	}
}
