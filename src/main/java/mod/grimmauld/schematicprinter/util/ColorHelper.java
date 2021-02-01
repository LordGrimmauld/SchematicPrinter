package mod.grimmauld.schematicprinter.util;

import net.minecraft.util.math.Vec3d;

public class ColorHelper {
	public static Vec3d getRGB(int color) {
		int r = color >> 16;
		int g = color >> 8 & 255;
		int b = color & 255;
		return (new Vec3d(r, g, b)).scale(0.00390625D);
	}
}
