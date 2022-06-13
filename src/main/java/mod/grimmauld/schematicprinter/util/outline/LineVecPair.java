package mod.grimmauld.schematicprinter.util.outline;


import net.minecraft.world.phys.Vec3;

public class LineVecPair {
	public Vec3 first;
	public Vec3 second;

	private LineVecPair(Vec3 from, Vec3 to) {
		if (from.hashCode() < to.hashCode()) {
			first = from;
			second = to;
		} else {
			first = to;
			second = from;
		}
	}

	public static LineVecPair of(Vec3 from, Vec3 to) {
		return new LineVecPair(from, to);
	}

	@Override
	public int hashCode() {
		return (this.first == null ? 0 : this.first.hashCode() << 5) ^ (this.second == null ? 0 : this.second.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LineVecPair)) return false;
		LineVecPair that = (LineVecPair) o;
		return first.x == that.first.x && first.y == that.first.y && first.z == that.first.z &&
			second.x == that.second.x && second.y == that.second.y && second.z == that.second.z;
	}

	@Override
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}

	public Vec3 getFirst() {
		return first;
	}

	public Vec3 getSecond() {
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
