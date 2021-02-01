package mod.grimmauld.schematicprinter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VecHelper {
	public static final Vec3d CENTER_OF_ORIGIN = new Vec3d(0.5D, 0.5D, 0.5D);

	public static Vec3d rotate(Vec3d vec, double deg, Direction.Axis axis) {
		if (deg == 0.0D) {
			return vec;
		} else if (vec == Vec3d.ZERO) {
			return vec;
		} else {
			float angle = (float) (deg / 180.0D * 3.141592653589793D);
			double sin = MathHelper.sin(angle);
			double cos = MathHelper.cos(angle);
			double x = vec.x;
			double y = vec.y;
			double z = vec.z;
			if (axis == Direction.Axis.X) {
				return new Vec3d(x, y * cos - z * sin, z * cos + y * sin);
			} else if (axis == Direction.Axis.Y) {
				return new Vec3d(x * cos + z * sin, y, z * cos - x * sin);
			} else {
				return axis == Direction.Axis.Z ? new Vec3d(x * cos - y * sin, y * cos + x * sin, z) : vec;
			}
		}
	}

	public static Vec3d getCenterOf(Vec3i pos) {
		return pos.equals(Vec3i.NULL_VECTOR) ? CENTER_OF_ORIGIN : (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D);
	}

	public static Vec3d axisAlingedPlaneOf(Vec3d vec) {
		vec = vec.normalize();
		return (new Vec3d(1.0D, 1.0D, 1.0D)).subtract(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
	}
}
