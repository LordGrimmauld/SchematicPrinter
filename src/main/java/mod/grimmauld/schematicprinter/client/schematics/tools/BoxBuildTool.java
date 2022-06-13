package mod.grimmauld.schematicprinter.client.schematics.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import mod.grimmauld.schematicprinter.client.schematics.select.SelectBox;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoxBuildTool extends SelectBox {
	private final Supplier<Optional<BlockState>> stateGen;

	public BoxBuildTool(Component description, Supplier<Optional<BlockState>> stateGen, int color, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, color, pos1, pos2);
		this.stateGen = stateGen;
	}

	public BoxBuildTool(Component description, Supplier<Optional<BlockState>> stateGen, SelectConfig<BlockPos> pos1, SelectConfig<BlockPos> pos2) {
		super(description, pos1, pos2);
		this.stateGen = stateGen;
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
