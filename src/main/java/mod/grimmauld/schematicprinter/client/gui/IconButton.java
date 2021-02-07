package mod.grimmauld.schematicprinter.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.Widget;

import java.util.LinkedList;
import java.util.List;

public class IconButton extends Widget {
	private final GuiIcons icon;
	protected final List<String> toolTip = new LinkedList<>();
	protected boolean pressed;

	public IconButton(int x, int y, GuiIcons icon) {
		super(x, y, 18, 18, "");
		this.icon = icon;
	}

	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			GuiTextures button = !this.pressed && this.active ? (this.isHovered ? GuiTextures.BUTTON_HOVER : GuiTextures.BUTTON) : GuiTextures.BUTTON_DOWN;
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GuiTextures.BUTTON.bind();
			this.blit(this.x, this.y, button.startX, button.startY, button.width, button.height);
			this.icon.draw(this, this.x + 1, this.y + 1);
		}

	}

	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		super.onClick(p_onClick_1_, p_onClick_3_);
		this.pressed = true;
	}

	public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
		super.onRelease(p_onRelease_1_, p_onRelease_3_);
		this.pressed = false;
	}

	public void setToolTip(String text) {
		this.toolTip.clear();
		this.toolTip.add(text);
	}
}