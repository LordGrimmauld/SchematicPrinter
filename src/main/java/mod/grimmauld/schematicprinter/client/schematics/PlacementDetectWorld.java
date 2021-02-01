package mod.grimmauld.schematicprinter.client.schematics;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.client.printer.Printer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

@MethodsReturnNonnullByDefault
public class PlacementDetectWorld extends SchematicWorld {
	public PlacementDetectWorld(BlockPos anchor, World original) {
		super(anchor, original);
	}

	public PlacementDetectWorld(World original) {
		super(original);
	}

	@Override
	public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState state, int arg2) {
		pos = pos.subtract(anchor);
		Printer.printQueue.add(new BlockInformation(pos, state));
		return true;
	}
}
