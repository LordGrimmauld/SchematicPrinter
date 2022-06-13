package mod.grimmauld.schematicprinter.util.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Stream;

public class CollectionOutline extends Outline {
	private Set<LineVecPair> edges;
	private Iterator<LineVecPair> edgesToProcess;

	public CollectionOutline() {
		edges = new HashSet<>();
		edgesToProcess = Collections.emptyIterator();
	}

	@Override
	public void render(PoseStack ms, SuperRenderTypeBuffer buffer) {
		long startTime = System.currentTimeMillis();
		while (edgesToProcess.hasNext() && (System.currentTimeMillis() - startTime) < 40) {
			LineVecPair line = edgesToProcess.next();
			if (!edges.remove(line))
				edges.add(line);
		}

		edges.forEach(line -> this.renderAACuboidLine(ms, buffer, line.getFirst(), line.getSecond()));
	}

	public CollectionOutline withPositions(Stream<BlockPos> positions) {
		return withPositions(positions, 42);
	}

	public CollectionOutline withPositions(Stream<BlockPos> positions, int initialCap) {
		edges = new HashSet<>(initialCap);
		edgesToProcess = positions.flatMap(this::addVerticeForPos).iterator();
		return this;
	}

	private Stream<LineVecPair> addVerticeForPos(BlockPos pos) {
		Vec3 xyz = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		Vec3 Xyz = new Vec3(pos.getX() + 1, pos.getY(), pos.getZ());
		Vec3 xYz = new Vec3(pos.getX(), pos.getY() + 1, pos.getZ());
		Vec3 XYz = new Vec3(pos.getX() + 1, pos.getY() + 1, pos.getZ());
		Vec3 xyZ = new Vec3(pos.getX(), pos.getY(), pos.getZ() + 1);
		Vec3 XyZ = new Vec3(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
		Vec3 xYZ = new Vec3(pos.getX(), pos.getY() + 1, pos.getZ() + 1);
		Vec3 XYZ = new Vec3(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		return Stream.of(
			LineVecPair.of(xyz, Xyz), LineVecPair.of(xyz, xYz),
			LineVecPair.of(xyz, xyZ), LineVecPair.of(XyZ, xyZ),
			LineVecPair.of(XyZ, XYZ), LineVecPair.of(XyZ, Xyz),
			LineVecPair.of(XYz, xYz), LineVecPair.of(XYz, Xyz),
			LineVecPair.of(XYz, XYZ), LineVecPair.of(xYZ, XYZ),
			LineVecPair.of(xYZ, xyZ), LineVecPair.of(xYZ, xYz));
	}

	@Override
	public Outline extendedUpwards(int value) {
		edges.stream().max(Comparator.comparing(LineVecPair::getMaxY)).map(LineVecPair::getMaxY).ifPresent(maxY -> edges.forEach(edge -> edge.extendUpwards(value - 1, maxY)));
		return super.extendedUpwards(value);
	}
}
