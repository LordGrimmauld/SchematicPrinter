package mod.grimmauld.schematicprinter.client.printer;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockInformation {
	public final BlockState state;
	public final BlockPos pos;

	public BlockInformation(BlockPos pos, BlockState state) {
		this.pos = pos;
		this.state = state;
	}

	public String getPrintCommand() {
		String blockstring = state.toString().replaceFirst("Block\\{", "").replaceFirst("}", "");

		return "/setblock " + pos.getX() + " " + pos.getY() + " "
			+ pos.getZ() + " " + blockstring;
	}
}
