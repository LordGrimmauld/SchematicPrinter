package mod.grimmauld.schematicprinter.client.schematics.tools;

import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;

import java.util.Optional;
import java.util.function.Supplier;

public class BuildToolStateSupplier {
	public static final Supplier<Optional<BlockState>> CLEAR = () -> Optional.of(Blocks.AIR.defaultBlockState());
	private static final Minecraft MC = Minecraft.getInstance();
	public static final Supplier<Optional<BlockState>> FILL_FROM_PALETTE = () -> PaletteManager.getRandomBlockState(MC.level);
}
