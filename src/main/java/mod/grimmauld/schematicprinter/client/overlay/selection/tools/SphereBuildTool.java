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
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SphereBuildTool extends AbstractSelectTool {
	private final BlockPosSelectConfig anchor;
	private final IntSelectConfig radius;
	private final Supplier<Optional<BlockState>> stateGen;

	public SphereBuildTool(ITextComponent description, BlockPosSelectConfig anchor, IntSelectConfig radius, Supplier<Optional<BlockState>> stateGen) {
		super(description, 0x6886c5);
		this.anchor = anchor;
		this.radius = radius;
		this.stateGen = stateGen;

		anchor.registerChangeListener(this::invalidateOutline);
		radius.registerChangeListener(this::invalidateOutline);
	}

	@Nullable
	@Override
	protected Outline getUpdatedOutline() {
		if (outline != null)
			return outline;
		return new CollectionOutline().withPositions(getPositions());
	}

	@Override
	protected Stream<BlockPos> getPositions() {
		BlockPos anchorPos = anchor.getPos();
		if (anchorPos == null)
			return Stream.empty();

		return IntStream.range(-radius.value, +radius.value).boxed().flatMap(x ->
			IntStream.range(-radius.value, +radius.value).boxed().flatMap(y ->
				IntStream.range(-radius.value, +radius.value)
					.filter(z -> x * x + y * y + z * z < radius.value * radius.value)
					.mapToObj(z -> anchorPos.add(x, y, z))));
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		Printer.addAll(putBlocksInBox(stateGen));
		Printer.startPrinting();
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