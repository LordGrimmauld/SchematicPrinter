package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectSchematicSave extends SelectBox{
	public SelectSchematicSave(ITextComponent description, int color, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, color, pos1, pos2);
	}

	public SelectSchematicSave(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		System.out.println("save schematic");
	}
}
