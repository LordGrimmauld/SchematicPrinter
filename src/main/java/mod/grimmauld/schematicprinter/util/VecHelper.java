package mod.grimmauld.schematicprinter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class VecHelper {
	public static final Vector3d CENTER_OF_ORIGIN = new Vector3d(0.5D, 0.5D, 0.5D);

	public static Vector3d rotate(Vector3d vec, double deg, Direction.Axis axis) {
		if (deg == 0.0D) {
			return vec;
		} else if (vec == Vector3d.ZERO) {
			return vec;
		} else {
			float angle = (float) (deg / 180.0D * 3.141592653589793D);
			double sin = MathHelper.sin(angle);
			double cos = MathHelper.cos(angle);
			double x = vec.x;
			double y = vec.y;
			double z = vec.z;
			if (axis == Direction.Axis.X) {
				return new Vector3d(x, y * cos - z * sin, z * cos + y * sin);
			} else if (axis == Direction.Axis.Y) {
				return new Vector3d(x * cos + z * sin, y, z * cos - x * sin);
			} else {
				return axis == Direction.Axis.Z ? new Vector3d(x * cos - y * sin, y * cos + x * sin, z) : vec;
			}
		}
	}

	public static Vector3d getCenterOf(Vector3i pos) {
		return pos.equals(Vector3i.NULL_VECTOR) ? CENTER_OF_ORIGIN : Vector3d.copy(pos).add(0.5D, 0.5D, 0.5D);
	}

	public static Vector3d axisAlingedPlaneOf(Vector3d vec) {
		vec = vec.normalize();
		return (new Vector3d(1.0D, 1.0D, 1.0D)).subtract(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
	}
}
