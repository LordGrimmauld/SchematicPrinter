package mod.grimmauld.schematicprinter.client.schematics.select;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.schematics.SchematicMetaInf;
import mod.grimmauld.schematicprinter.util.outline.LineOutline;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RotateTool extends SchematicToolBase {
	private final LineOutline line = new LineOutline();

	public RotateTool(Component description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		if (schematicHandler.activeSchematic != null)
			schematicHandler.activeSchematic.transformation
				.rotate90(true);
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		if (schematicHandler.activeSchematic != null)
			schematicHandler.activeSchematic.transformation
				.rotate90(delta > 0);
		return true;
	}

	@Override
	public void renderOnSchematic(PoseStack ms, SuperRenderTypeBuffer buffer) {
		SchematicMetaInf inf = schematicHandler.activeSchematic;
		if (inf == null || !schematicHandler.isDeployed())
			return;

		double height = inf.bounds.getYsize() + Math.max(20, inf.bounds.getYsize());
		Vec3 center = inf.bounds.getCenter()
			.add(inf.transformation
				.getRotationOffset(false));
		Vec3 start = center.subtract(0, height / 2, 0);
		Vec3 end = center.add(0, height / 2, 0);

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
