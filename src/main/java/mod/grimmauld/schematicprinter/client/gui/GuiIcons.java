package mod.grimmauld.schematicprinter.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GuiIcons {
	public static final ResourceLocation ICON_ATLAS = new ResourceLocation(SchematicPrinter.MODID, "textures/gui/icons.png");
	public static final GuiIcons I_TRASH = new GuiIcons(1, 0);
	public static final GuiIcons I_CONFIRM = new GuiIcons(0, 1);
	public static final GuiIcons I_OPEN_FOLDER = new GuiIcons(2, 1);
	private final int iconX;
	private final int iconY;

	public GuiIcons(int x, int y) {
		this.iconX = x * 16;
		this.iconY = y * 16;
	}

	@OnlyIn(Dist.CLIENT)
	public void bind() {
		Minecraft.getInstance().getTextureManager().bind(ICON_ATLAS);
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(MatrixStack ms, AbstractGui screen, int x, int y) {
		this.bind();
		screen.blit(ms, x, y, this.iconX, this.iconY, 16, 16);
	}
}
