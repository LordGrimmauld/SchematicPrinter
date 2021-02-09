package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectBox;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoxBuildTool extends SelectBox {
	private final Supplier<Optional<BlockState>> stateGen;

	public BoxBuildTool(ITextComponent description, Supplier<Optional<BlockState>> stateGen, int color, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, color, pos1, pos2);
		this.stateGen = stateGen;
	}

	public BoxBuildTool(ITextComponent description, Supplier<Optional<BlockState>> stateGen, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, pos1, pos2);
		this.stateGen = stateGen;
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
