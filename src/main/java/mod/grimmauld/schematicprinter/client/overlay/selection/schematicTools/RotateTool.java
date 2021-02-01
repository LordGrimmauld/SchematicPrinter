package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.outline.LineOutline;
import net.minecraft.util.math.Vec3d;

public class RotateTool extends PlacementToolBase {
	private final LineOutline line = new LineOutline();

	@Override
	public boolean handleActivated() {
		if (schematicHandler.activeSchematic != null)
			schematicHandler.activeSchematic.transformation
				.rotate90(true);
		return true;
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		if (schematicHandler.activeSchematic != null)
			schematicHandler.activeSchematic.transformation
				.rotate90(delta > 0);
		return true;
	}

	@Override
	public void renderOnSchematic(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (inf == null)
			return;

		double height = inf.bounds.getYSize() + Math.max(20, inf.bounds.getYSize());
		Vec3d center = inf.bounds.getCenter()
			.add(inf.transformation
				.getRotationOffset(false));
		Vec3d start = center.subtract(0, height / 2, 0);
		Vec3d end = center.add(0, height / 2, 0);

		line.getParams()
			.disableCull()
			.disableNormals()
			.colored(0xdddddd)
			.lineWidth(1 / 16f);
		line.set(start, end)
			.render(ms, buffer);

		super.renderOnSchematic(ms, buffer);
	}
}
