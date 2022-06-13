package mod.grimmauld.schematicprinter.client.schematics.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.util.outline.CollectionOutline;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.NonNullSelectConfig;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SphereBuildTool extends AbstractSelectTool {
	private final SelectConfig<BlockPos> anchor;
	private final NonNullSelectConfig<Integer> radius;
	private final Supplier<Optional<BlockState>> stateGen;

	public SphereBuildTool(Component description, SelectConfig<BlockPos> anchor, NonNullSelectConfig<Integer> radius, Supplier<Optional<BlockState>> stateGen) {
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
		return new CollectionOutline().withPositions(getPositions(), 6 * radius.getValue() * radius.getValue() * radius.getValue());
	}

	@Override
	protected Stream<BlockPos> getPositions() {
		BlockPos anchorPos = anchor.getValue();
		if (anchorPos == null)
			return Stream.empty();

		return IntStream.range(-radius.getValue(), +radius.getValue()).boxed().flatMap(y ->
			IntStream.range(-radius.getValue(), +radius.getValue()).boxed().flatMap(x ->
				IntStream.range(-radius.getValue(), +radius.getValue())
					.filter(z -> x * x + y * y + z * z < radius.getValue() * radius.getValue())
					.mapToObj(z -> anchorPos.offset(x, y, z))));
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		Printer.addAll(putBlocksInBox(stateGen));
		Printer.startPrinting();
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);

		if (stateGen == BuildToolStateSupplier.FILL_FROM_PALETTE)
			SchematicPrinterClient.paletteOverlay.render(event);
	}

}