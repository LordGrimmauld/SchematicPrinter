package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import net.minecraft.block.Blocks;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClearTool extends BoxBuildTool {
	public ClearTool(ITextComponent description, int color, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, color, pos1, pos2);
	}

	public ClearTool(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	protected Stream<BlockInformation> putBlocksInBox() {
		return getPositions().map(pos -> new BlockInformation(pos, Blocks.AIR.getDefaultState()));
	}
}
