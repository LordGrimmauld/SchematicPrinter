package mod.grimmauld.schematicprinter.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class UtilScreen extends Screen {
	protected static final Minecraft MC = Minecraft.getInstance();
	protected final List<Widget> widgets;
	protected int sWidth, sHeight;
	protected int guiLeft, guiTop;

	protected UtilScreen() {
		super(new StringTextComponent(""));
		widgets = new ArrayList<>();
	}

	protected void setWindowSize(int width, int height) {
		sWidth = width;
		sHeight = height;
		guiLeft = (this.width - sWidth) / 2;
		guiTop = (this.height - sHeight) / 2;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		renderWindow(mouseX, mouseY, partialTicks);
		for (Widget widget : widgets) {
			widget.render(mouseX, mouseY, partialTicks);
			widget.renderToolTip(mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		boolean result = false;
		for (Widget widget : widgets) {
			if (widget.mouseClicked(x, y, button))
				result = true;
		}
		return result;
	}

	@Override
	public boolean keyPressed(int code, int p_keyPressed_2_, int p_keyPressed_3_) {
		for (Widget widget : widgets) {
			if (widget.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_))
				return true;
		}
		return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char character, int code) {
		for (Widget widget : widgets) {
			if (widget.charTyped(character, code))
				return true;
		}
		if (character == 'e')
			onClose();
		return super.charTyped(character, code);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		for (Widget widget : widgets) {
			if (widget.mouseScrolled(mouseX, mouseY, delta))
				return true;
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		boolean result = false;
		for (Widget widget : widgets) {
			if (widget.mouseReleased(x, y, button))
				result = true;
		}
		return result | super.mouseReleased(x, y, button);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected abstract void renderWindow(int mouseX, int mouseY, float partialTicks);
}
