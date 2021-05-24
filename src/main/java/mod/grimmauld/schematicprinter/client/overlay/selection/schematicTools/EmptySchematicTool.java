package mod.grimmauld.schematicprinter.client.overlay.selection.schematicTools;

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
