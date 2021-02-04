package mod.grimmauld.schematicprinter.util.outline;

import net.minecraft.util.math.Vec3d;

public class LineVecPair {
	public final Vec3d first;
	public final Vec3d second;

	private LineVecPair(Vec3d from, Vec3d to) {
		if (from.hashCode() < to.hashCode()) {
			first = from;
			second = to;
		} else {
			first = to;
			second = from;
		}
	}

	public static LineVecPair of(Vec3d from, Vec3d to) {
		return new LineVecPair(from, to);
	}

	@Override
	public int hashCode() {
		return (this.first == null ? 0 : this.first.hashCode()) ^ (this.second == null ? 0 : this.second.hashCode());
	}

	@Override
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
}
