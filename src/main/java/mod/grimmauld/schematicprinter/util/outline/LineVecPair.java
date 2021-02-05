package mod.grimmauld.schematicprinter.util.outline;


import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

public class LineVecPair {
	public Vector3d first;
	public Vector3d second;

	private LineVecPair(Vector3d from, Vector3d to) {
		if (from.hashCode() < to.hashCode()) {
			first = from;
			second = to;
		} else {
			first = to;
			second = from;
		}
	}

	public static LineVecPair of(Vector3d from, Vector3d to) {
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

	public Vector3d getFirst() {
		return first;
	}

	public Vector3d getSecond() {
		return second;
	}

	public double getMaxY() {
		return Math.max(first.y, second.y);
	}

	public void extendUpwards(int value, double maxY) {
		if (first.y == maxY)
			first = first.add(0, value, 0);
		if (second.y == maxY)
			second = second.add(0, value, 0);
	}
}
