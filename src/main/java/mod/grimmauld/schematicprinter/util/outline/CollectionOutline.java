package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CollectionOutline extends Outline {
	private final Set<BlockPos> positions;
	private final Set<LineVecPair> edges;

	public CollectionOutline() {
		this.positions = new HashSet<>();
		edges = new HashSet<>();
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		edges.forEach(line -> this.renderAACuboidLine(ms, buffer, line.first, line.second));
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
	}

	private void addVertices(Vec3d xyz, Vec3d xYz, Vec3d XYz, Vec3d xyZ, Vec3d xYZ, Vec3d XYZ) {
		addOrDeleteVertex(XYz, xYz);
		addOrDeleteVertex(XYz, xyz);
		addOrDeleteVertex(XYz, XYZ);
		addOrDeleteVertex(xYZ, XYZ);
		addOrDeleteVertex(xYZ, xyZ);
		addOrDeleteVertex(xYZ, xYz);
	}

	private void addOrDeleteVertex(Vec3d from, Vec3d to) {
		LineVecPair pair = LineVecPair.of(from, to);
		if (edges.contains(pair))
			edges.remove(pair);
		else
			edges.add(pair);
	}
}
