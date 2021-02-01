package mod.grimmauld.schematicprinter.client;

import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public enum ExtraTextures {
	BLANK("blank.png"),
	GRAY("background.png"),
	CHECKERED("checkerboard.png"),
	HIGHLIGHT_CHECKERED("highlighted_checkerboard.png");

	public static final String ASSET_PATH = "textures/special/";
	private final ResourceLocation location;

	ExtraTextures(String filename) {
		this.location = new ResourceLocation(SchematicPrinter.MODID, ASSET_PATH + filename);
	}

	public ResourceLocation getLocation() {
		return this.location;
	}
}
