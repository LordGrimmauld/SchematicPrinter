package mod.grimmauld.schematicprinter.client.palette;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PaletteManager {
	public static final Map<BlockState, Integer> PALETTE = new HashMap<>();

	public static void clearPalette() {
		PALETTE.clear();
	}

	public static void addToPalette(BlockState state) {
		increaseWeight(state, 1);
	}

	public static void increaseWeight(BlockState state, int weight) {
		PALETTE.put(state, Math.min(99, PALETTE.getOrDefault(state, 0) + weight));
	}

	public static void decreaseWeight(BlockState state) {
		decreaseWeight(state, 1);
	}

	public static void decreaseWeight(BlockState state, int weight) {
		if (!PALETTE.containsKey(state))
			return;
		int newWeight = PALETTE.get(state) - Math.abs(weight);
		if (newWeight > 0)
			PALETTE.put(state, newWeight);
		else
			PALETTE.remove(state);
	}

	public static void decreaseWeight(Block block, int weight) {
		PALETTE.keySet().stream().filter(state -> state.getBlock().equals(block)).collect(Collectors.toSet()).forEach(state -> decreaseWeight(state, weight));
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
		int weights = PALETTE.values().stream().mapToInt(Integer::intValue).sum();
		if (weights == 0)
			return Optional.empty();
		int choice = random.nextInt(weights);
		for (Map.Entry<BlockState, Integer> paletteBlock : PALETTE.entrySet()) {
			choice -= paletteBlock.getValue();
			if (choice < 0)
				return Optional.of(paletteBlock.getKey());
		}
		return Optional.empty();
	}

	public static boolean containsState(BlockState state) {
		return PALETTE.containsKey(state);
	}

	public static boolean containsBlock(Block block) {
		return PALETTE.keySet().stream().map(BlockState::getBlock).anyMatch(block::equals);
	}

	public static void removeFromPalette(Block block) {
		PALETTE.keySet().stream().filter(state -> state.getBlock().equals(block)).collect(Collectors.toSet()).forEach(PALETTE::remove);
	}
}
