package mod.grimmauld.schematicprinter.client.printer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.core.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockInformation implements Comparable<BlockInformation> {
	public final BlockState state;
	public final BlockPos pos;
	private final LazyLoadedValue<String> printCommand;
	public boolean overrideAir = true;

	public BlockInformation(BlockPos pos, BlockState state) {
		this.pos = pos;
		this.state = state;
		this.printCommand = new LazyLoadedValue<>(() -> "/setblock " + pos.getX() + " " + pos.getY() + " "
			+ pos.getZ() + " " + state.toString().replaceFirst("Block\\{", "").replaceFirst("}", ""));
	}

	public String getPrintCommand() {
		return printCommand.get();
	}

	@Override
	public int compareTo(BlockInformation o) {
		return getPrintCommand().compareTo(o.getPrintCommand());
	}

	public BlockInformation setOverrideAir(boolean overrideAir) {
		this.overrideAir = overrideAir;
		return this;
	}
}
