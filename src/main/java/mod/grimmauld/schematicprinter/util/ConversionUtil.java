package mod.grimmauld.schematicprinter.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class ConversionUtil {
	// private constructor, no instances
	private ConversionUtil() {}

	public static BlockPos Vec3iToBlockPos(Vec3i vec) {
		return new BlockPos(vec.getX(), vec.getY(), vec.getZ());
	}
}