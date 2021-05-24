package mod.grimmauld.schematicprinter.client.schematics.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.util.outline.CollectionOutline;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.NonNullSelectConfig;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CircleBuildTool extends AbstractSelectTool {
	private final SelectConfig<BlockPos> anchor;
	private final NonNullSelectConfig<Integer> radius;
	private final NonNullSelectConfig<Integer> height;
	private final Supplier<Optional<BlockState>> stateGen;

	public CircleBuildTool(ITextComponent description, SelectConfig<BlockPos> anchor, NonNullSelectConfig<Integer> radius, NonNullSelectConfig<Integer> height, Supplier<Optional<BlockState>> stateGen) {
		super(description, 0x6886c5);
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
		return new CollectionOutline().withPositions(getBaseLayerPositions(), 4 * radius.getValue() * radius.getValue()).extendedUpwards(height.getValue());
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		Printer.addAll(putBlocksInBox(stateGen));
		Printer.startPrinting();
	}

	private Stream<BlockPos> getBaseLayerPositions() {
		BlockPos anchorPos = anchor.getValue();
		if (anchorPos == null)
			return Stream.empty();

		return IntStream.range(-radius.getValue(), +radius.getValue() + 1).boxed().flatMap(x ->
			IntStream.range(-radius.getValue(), +radius.getValue() + 1)
				.filter(z -> x * x + z * z - Math.abs(x) - Math.abs(z) < radius.getValue() * radius.getValue())
				.mapToObj(z -> anchorPos.add(x, 0, z)));
	}

	@Override
	protected Stream<BlockPos> getPositions() {
		return IntStream.range(0, height.getValue()).boxed().flatMap(y -> getBaseLayerPositions().map(pos -> pos.add(0, y, 0)));
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);

		if (stateGen == BuildToolStateSupplier.FILL_FROM_PALETTE)
			SchematicPrinterClient.paletteOverlay.render(event);
	}
}
