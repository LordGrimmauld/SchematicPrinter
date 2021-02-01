package mod.grimmauld.schematicprinter.util;

public class AngleHelper {
	public static float deg(double angle) {
		return angle == 0.0D ? 0.0F : (float) (angle * 180.0D / 3.141592653589793D);
	}

	public static float angleLerp(double pct, double current, double target) {
		return (float) (current + (double) getShortestAngleDiff(current, target) * pct);
	}

	public static float getShortestAngleDiff(double current, double target) {
		current %= 360.0D;
		target %= 360.0D;
		return (float) (((target - current) % 360.0D + 540.0D) % 360.0D - 180.0D);
	}
}
