package mod.grimmauld.schematicprinter.util.outline;

import net.minecraft.util.math.Vec3d;

import java.util.Objects;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LineVecPair)) return false;
		LineVecPair that = (LineVecPair) o;
		return Objects.equals(first, that.first) && Objects.equals(second, that.second);
	}

	@Override
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}

	public Vec3d getFirst() {
		return first;
	}

	public Vec3d getSecond() {
		return second;
	}
}
