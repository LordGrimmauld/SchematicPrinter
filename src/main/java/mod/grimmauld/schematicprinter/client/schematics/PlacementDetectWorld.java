package mod.grimmauld.schematicprinter.client.schematics;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@MethodsReturnNonnullByDefault
public class PlacementDetectWorld extends SchematicWorld {
	private final HashSet<BlockInformation> bufferedSchematic;

	public PlacementDetectWorld(Level original) {
		super(original);
		bufferedSchematic = new HashSet<>();
	}

	@Override
	public boolean setBlock(@Nonnull BlockPos pos, @Nonnull BlockState state, int arg2) {
		pos = pos.subtract(anchor);
		bufferedSchematic.add(new BlockInformation(pos, state).setOverrideAir(Printer.shouldReplaceBlocks));
		return true;
	}

	public void printBuffer() {
		Printer.addAll(((Set<BlockInformation>) bufferedSchematic.clone()).stream());
		bufferedSchematic.clear();
	}

	public void clearBuffer() {
		bufferedSchematic.clear();
	}
}
