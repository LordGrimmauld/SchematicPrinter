package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;

import java.util.Optional;
import java.util.function.Supplier;

public class BuildToolStateSupplier {
	private static final Minecraft MC = Minecraft.getInstance();

	public static final Supplier<Optional<BlockState>> CLEAR = () -> Optional.of(Blocks.AIR.getDefaultState());
	public static final Supplier<Optional<BlockState>> FILL_FROM_PALETTE = () -> PaletteManager.getRandomBlockState(MC.world);
}
