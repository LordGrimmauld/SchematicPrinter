package mod.grimmauld.schematicprinter.client.gui;

import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum GuiTextures {
	SCHEMATIC_PROMPT("save_gui.png", 213, 77),
	BUTTON("widgets.png", 18, 18),
	BUTTON_HOVER("widgets.png", 18, 0, 18, 18),
	BUTTON_DOWN("widgets.png", 36, 0, 18, 18);

	public static final int FONT_COLOR = 5726074;
	public final ResourceLocation location;
	public final int width;
	public final int height;
	public final int startX;
	public final int startY;

	GuiTextures(String location, int width, int height) {
		this(location, 0, 0, width, height);
	}

	GuiTextures(String location, int startX, int startY, int width, int height) {
		this.location = new ResourceLocation(SchematicPrinter.MODID, "textures/gui/" + location);
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
	}

	@OnlyIn(Dist.CLIENT)
	public void bind() {
		Minecraft.getInstance().getTextureManager().bindTexture(this.location);
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(AbstractGui screen, int x, int y) {
		this.bind();
		screen.blit(x, y, this.startX, this.startY, this.width, this.height);
	}
}
