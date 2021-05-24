package mod.grimmauld.schematicprinter.client.schematics.select;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmptySchematicTool extends SchematicToolBase {
	public static final EmptySchematicTool INSTANCE = new EmptySchematicTool();

	public EmptySchematicTool() {
		super(new StringTextComponent(""));
	}
}
