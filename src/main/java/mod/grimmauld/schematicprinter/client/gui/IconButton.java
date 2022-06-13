package mod.grimmauld.schematicprinter.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.LinkedList;
import java.util.List;

public class IconButton extends AbstractWidget {
	protected final List<Component> toolTip = new LinkedList<>();
	private final GuiIcons icon;
	protected boolean pressed;

	public IconButton(int x, int y, GuiIcons icon) {
		super(x, y, 18, 18, TextComponent.EMPTY);
		this.icon = icon;
	}

	public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			GuiTextures button = !this.pressed && this.active ? (this.isHovered ? GuiTextures.BUTTON_HOVER : GuiTextures.BUTTON) : GuiTextures.BUTTON_DOWN;
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			GuiTextures.BUTTON.bind();
			this.blit(ms, this.x, this.y, button.startX, button.startY, button.width, button.height);
			this.icon.draw(ms, this, this.x + 1, this.y + 1);
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

	public void setToolTip(Component text) {
		this.toolTip.clear();
		this.toolTip.add(text);
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
		if (this.isHovered) {
			p_169152_.add(NarratedElementType.POSITION, this.toolTip.toArray(new Component[0]));
		}
	}
}