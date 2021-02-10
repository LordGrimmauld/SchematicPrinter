package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class CollectionOutline extends Outline {
	private final ConcurrentHashMap<LineVecPair, AtomicInteger> edges;

	public CollectionOutline() {
		edges = new ConcurrentHashMap<>();
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		edges.forEach((line, n) -> this.renderAACuboidLine(ms, buffer, line.getFirst(), line.getSecond()));
	}

	public CollectionOutline withPositions(Stream<BlockPos> positions) {
		edges.clear();
		positions.parallel().forEach(this::addVerticeForPos);
		edges.entrySet().stream().filter(entry -> entry.getValue().get() % 2 == 0).forEach(entry -> edges.remove(entry.getKey()));
		return this;
	}

	private void addVerticeForPos(BlockPos pos) {
		Vec3d xyz = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		Vec3d Xyz = new Vec3d(pos.getX() + 1, pos.getY(), pos.getZ());
		Vec3d xYz = new Vec3d(pos.getX(), pos.getY() + 1, pos.getZ());
		Vec3d XYz = new Vec3d(pos.getX() + 1, pos.getY() + 1, pos.getZ());
		Vec3d xyZ = new Vec3d(pos.getX(), pos.getY(), pos.getZ() + 1);
		Vec3d XyZ = new Vec3d(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
		Vec3d xYZ = new Vec3d(pos.getX(), pos.getY() + 1, pos.getZ() + 1);
		Vec3d XYZ = new Vec3d(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		addVertices(xYz, Xyz, xyz, XYZ, XyZ, xyZ);
		addVertices(Xyz, xYz, XYz, xyZ, xYZ, XYZ);
	}

	private void addVertices(Vec3d xyz, Vec3d xYz, Vec3d XYz, Vec3d xyZ, Vec3d xYZ, Vec3d XYZ) {
		addVertex(XYz, xYz);
		addVertex(XYz, xyz);
		addVertex(XYz, XYZ);
		addVertex(xYZ, XYZ);
		addVertex(xYZ, xyZ);
		addVertex(xYZ, xYz);
	}

	private void addVertex(Vec3d from, Vec3d to) {
		LineVecPair pair = LineVecPair.of(from, to);
		AtomicInteger n = edges.get(pair);
		if (n != null)
			n.incrementAndGet();
		else
			edges.put(pair, new AtomicInteger(1));
	}

	@Override
	public Outline extendedUpwards(int value) {
		edges.keySet().stream().max(Comparator.comparing(LineVecPair::getMaxY)).map(LineVecPair::getMaxY).ifPresent(maxY -> edges.keySet().forEach(edge -> edge.extendUpwards(value - 1, maxY)));
		return super.extendedUpwards(value);
	}
}
