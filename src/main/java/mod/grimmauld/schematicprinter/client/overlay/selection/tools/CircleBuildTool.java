package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.IntSelectConfig;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.util.outline.CollectionOutline;
import mod.grimmauld.schematicprinter.util.outline.Outline;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CircleBuildTool extends AbstractSelectTool {
	private final BlockPosSelectConfig anchor;
	private final IntSelectConfig radius;
	private final IntSelectConfig height;
	private final Supplier<Optional<BlockState>> stateGen;

	public CircleBuildTool(String description, BlockPosSelectConfig anchor, IntSelectConfig radius, IntSelectConfig height, Supplier<Optional<BlockState>> stateGen) {
		super(new TranslationTextComponent(description), 0x6886c5);
		this.anchor = anchor;
		this.radius = radius;
		this.height = height;
		this.stateGen = stateGen;

		anchor.registerChangeListener(this::invalidateOutline);
		radius.registerChangeListener(this::invalidateOutline);
		height.registerChangeListener(this::invalidateOutline);
	}

	@Nullable
	@Override
	protected Outline getUpdatedOutline() {
		if (outline != null)
			return outline;
		return new CollectionOutline().withPositions(getBaseLayerPositions()).extendedUpwards(height.value);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		Printer.addAll(putBlocksInBox(stateGen));
		Printer.startPrinting();
	}

	private Stream<BlockPos> getBaseLayerPositions() {
		BlockPos anchorPos = anchor.getPos();
		if (anchorPos == null)
			return Stream.empty();

		return IntStream.range(-radius.value, +radius.value + 1).boxed().flatMap(x ->
			IntStream.range(-radius.value, +radius.value + 1)
				.filter(z -> x * x + z * z - Math.abs(x) - Math.abs(z) < radius.value * radius.value)
				.mapToObj(z -> anchorPos.add(x, 0, z)));
	}

	@Override
	protected Stream<BlockPos> getPositions() {
		return getBaseLayerPositions().flatMap(pos -> IntStream.range(0, height.value).mapToObj(y -> pos.add(0, y, 0)));
	}

	protected Stream<BlockInformation> putBlocksInBox(Supplier<Optional<BlockState>> stateGen) {
		return getPositions().flatMap(pos -> stateGen.get()
			.map(Stream::of).orElseGet(Stream::empty).map(state -> new BlockInformation(pos, state)));
	}

	@Override
	public boolean shouldRenderPalette() {
		return stateGen == BuildToolStateSupplier.FILL_FROM_PALETTE;
	}
}
