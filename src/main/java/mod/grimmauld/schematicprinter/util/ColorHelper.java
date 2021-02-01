package mod.grimmauld.schematicprinter.util;

import net.minecraft.util.math.vector.Vector3d;

public class ColorHelper {
	public static Vector3d getRGB(int color) {
		int r = color >> 16;
		int g = color >> 8 & 255;
		int b = color & 255;
		return (new Vector3d(r, g, b)).scale(0.00390625D);
	}
}
