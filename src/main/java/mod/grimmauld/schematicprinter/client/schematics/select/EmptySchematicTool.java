package mod.grimmauld.schematicprinter.client.schematics.select;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmptySchematicTool extends SchematicToolBase {
	public static final EmptySchematicTool INSTANCE = new EmptySchematicTool();

	public EmptySchematicTool() {
		super(new TextComponent(""));
	}
}
