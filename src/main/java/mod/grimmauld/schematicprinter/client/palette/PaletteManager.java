package mod.grimmauld.schematicprinter.client.palette;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PaletteManager {
	private static final Map<BlockState, Integer> PALETTE = new HashMap<>();

	static { // FIXME
		addToPalette(Blocks.STONE.getDefaultState());
	}

	public static void clearPalette() {
		PALETTE.clear();
	}

	public static void addToPalette(BlockState state) {
		addToPalette(state, 1);
	}

	public static void addToPalette(BlockState state, int weight) {
		PALETTE.put(state, PALETTE.getOrDefault(state, 0) + weight);
	}

	public static void decreaseWeight(BlockState state) {
		decreaseWeight(state, 1);
	}

	public static void decreaseWeight(BlockState state, int weight) {
		int newWeight = PALETTE.getOrDefault(state, 0) - weight;
		if (newWeight > 0)
			PALETTE.put(state, newWeight);
		else
			PALETTE.remove(state);
	}

	public static void removeFromPalette(BlockState state) {
		PALETTE.remove(state);
	}

	public static Optional<BlockState> getRandomBlockState(@Nullable IWorld world) {
		if (world == null)
			return Optional.empty();
		return getRandomBlockState(world.getRandom());
	}

	public static Optional<BlockState> getRandomBlockState(Random random) {
		int choice = random.nextInt(PALETTE.values().stream().mapToInt(Integer::intValue).sum());
		for (Map.Entry<BlockState, Integer> paletteBlock : PALETTE.entrySet()) {
			choice -= paletteBlock.getValue();
			if (choice < 0)
				return Optional.of(paletteBlock.getKey());
		}
		return Optional.empty();
	}
}
