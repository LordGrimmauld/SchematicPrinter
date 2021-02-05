package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CollectionOutline extends Outline {
	private final Set<BlockPos> positions;
	private final Set<LineVecPair> edges;
	private int upwardsExtension = 1;

	public CollectionOutline() {
		this.positions = new HashSet<>();
		edges = new HashSet<>();
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		edges.forEach(line -> this.renderAACuboidLine(ms, buffer, line.getFirst(), line.getSecond()));
	}

	public CollectionOutline withPositions(Collection<BlockPos> positions) {
		if (this.positions.addAll(positions))
			recalculateVertices();
		return this;
	}

	public CollectionOutline withPositions(Stream<BlockPos> positions) {
		AtomicBoolean changed = new AtomicBoolean(false);
		positions.filter(this.positions::add).forEach(pos -> changed.set(true));
		if (changed.get())
			recalculateVertices();
		return this;
	}

	private void recalculateVertices() {
		edges.clear();

		for (BlockPos pos : positions) {
			Vector3d xyz = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
			Vector3d Xyz = new Vector3d(pos.getX() + 1, pos.getY(), pos.getZ());
			Vector3d xYz = new Vector3d(pos.getX(), pos.getY() + 1, pos.getZ());
			Vector3d XYz = new Vector3d(pos.getX() + 1, pos.getY() + 1, pos.getZ());
			Vector3d xyZ = new Vector3d(pos.getX(), pos.getY(), pos.getZ() + 1);
			Vector3d XyZ = new Vector3d(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			Vector3d xYZ = new Vector3d(pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			Vector3d XYZ = new Vector3d(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
			addVertices(xYz, Xyz, xyz, XYZ, XyZ, xyZ);
			addVertices(Xyz, xYz, XYz, xyZ, xYZ, XYZ);
		}

		edges.stream().max(Comparator.comparing(LineVecPair::getMaxY)).map(LineVecPair::getMaxY).ifPresent(maxY -> edges.forEach(edge -> edge.extendUpwards(upwardsExtension - 1, maxY)));
	}

	private void addVertices(Vector3d xyz, Vector3d xYz, Vector3d XYz, Vector3d xyZ, Vector3d xYZ, Vector3d XYZ) {
		addOrDeleteVertex(XYz, xYz);
		addOrDeleteVertex(XYz, xyz);
		addOrDeleteVertex(XYz, XYZ);
		addOrDeleteVertex(xYZ, XYZ);
		addOrDeleteVertex(xYZ, xyZ);
		addOrDeleteVertex(xYZ, xYz);
	}

	private void addOrDeleteVertex(Vector3d from, Vector3d to) {
		LineVecPair pair = LineVecPair.of(from, to);
		if (edges.contains(pair))
			edges.remove(pair);
		else
			edges.add(pair);
	}

	@Override
	public Outline extendedUpwards(int value) {
		if (upwardsExtension != value) {
			upwardsExtension = value;
			recalculateVertices();
		}

		return super.extendedUpwards(value);
	}
}
