package mod.grimmauld.schematicprinter.client.schematics;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SchematicMetaInf {
	public final Template structure;
	public final AxisAlignedBB bounds;
	public final AABBOutline outline;
	public final SchematicTransformation transformation;

	private SchematicMetaInf(String name) {
		this.structure = Schematics.loadSchematic(name).orElseGet(Template::new);
		this.bounds = new AxisAlignedBB(BlockPos.ZERO, structure.getSize());
		this.outline = new AABBOutline(this.bounds);
		this.outline.getParams().colored(6850245).lineWidth(0.0625F);
		this.transformation = new SchematicTransformation();
		this.transformation.init(BlockPos.ZERO, new PlacementSettings(), this.bounds);
	}

	public static SchematicMetaInf load(String name) {
		return new SchematicMetaInf(name);
	}
}
