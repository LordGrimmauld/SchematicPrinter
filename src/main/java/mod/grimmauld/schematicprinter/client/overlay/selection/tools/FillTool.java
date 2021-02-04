package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FillTool extends BoxBuildTool {
	public FillTool(ITextComponent description, int color, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, color, pos1, pos2);
	}

	public FillTool(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	protected Stream<BlockInformation> putBlocksInBox() {
		return getPositions().flatMap(pos -> PaletteManager.getRandomBlockState(MC.world)
			.map(Stream::of).orElseGet(Stream::empty).map(state -> new BlockInformation(pos, state)));
	}
}
