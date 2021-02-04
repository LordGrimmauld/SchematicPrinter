package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectBox;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BoxBuildTool extends SelectBox {
	public BoxBuildTool(ITextComponent description, int color, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, color, pos1, pos2);
	}

	public BoxBuildTool(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	protected abstract Stream<BlockInformation> putBlocksInBox();

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		Printer.addAll(putBlocksInBox());
		Printer.startPrinting();
	}
}
