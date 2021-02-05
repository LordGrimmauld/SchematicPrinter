package mod.grimmauld.schematicprinter.client.printer;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockInformation implements Comparable<BlockInformation> {
	public final BlockState state;
	public final BlockPos pos;
	private final LazyValue<String> printCommand;

	public BlockInformation(BlockPos pos, BlockState state) {
		this.pos = pos;
		this.state = state;
		this.printCommand = new LazyValue<>(() -> "/setblock " + pos.getX() + " " + pos.getY() + " "
			+ pos.getZ() + " " + state.toString().replaceFirst("Block\\{", "").replaceFirst("}", ""));
	}

	public String getPrintCommand() {
		return printCommand.getValue();
	}

	@Override
	public int compareTo(BlockInformation o) {
		return getPrintCommand().compareTo(o.getPrintCommand());
	}
}
