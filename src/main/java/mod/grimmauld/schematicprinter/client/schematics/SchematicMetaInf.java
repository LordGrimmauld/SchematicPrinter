package mod.grimmauld.schematicprinter.client.schematics;

import mod.grimmauld.schematicprinter.util.ConversionUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.util.outline.AABBOutline;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SchematicMetaInf {
	public final StructureTemplate structure;
	public final AABB bounds;
	public final AABBOutline outline;
	public final SchematicTransformation transformation;

	private SchematicMetaInf(String name) {
		this.structure = Schematics.loadSchematic(name).orElseGet(StructureTemplate::new);
		this.bounds = new AABB(BlockPos.ZERO, ConversionUtil.Vec3iToBlockPos(structure.getSize()));
		this.outline = new AABBOutline(this.bounds);
		this.outline.getParams().colored(6850245).lineWidth(0.0625F);
		this.transformation = new SchematicTransformation();
		this.transformation.init(BlockPos.ZERO, new StructurePlaceSettings(), this.bounds);
	}

	public static SchematicMetaInf load(String name) {
		return new SchematicMetaInf(name);
	}
}
