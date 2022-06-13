package mod.grimmauld.schematicprinter.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.LinkedList;
import java.util.List;

public class SmartWidget extends AbstractWidget {
	protected final List<Component> toolTip = new LinkedList<>();

	public SmartWidget(int xIn, int yIn, int widthIn, int heightIn) {
		super(xIn, yIn, widthIn, heightIn, TextComponent.EMPTY);
	}

	public List<Component> getToolTip() {
		return this.toolTip;
	}

	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
		if (this.isHovered) {
			p_169152_.add(NarratedElementType.POSITION, this.toolTip.toArray(new Component[0]));
		}
	}
}