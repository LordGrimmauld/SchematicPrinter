package mod.grimmauld.schematicprinter.client.gui;

import net.minecraft.client.gui.widget.Widget;

import java.util.LinkedList;
import java.util.List;

public class SmartWidget extends Widget {
	protected final List<String> toolTip = new LinkedList<>();

	public SmartWidget(int xIn, int yIn, int widthIn, int heightIn) {
		super(xIn, yIn, widthIn, heightIn, "");
	}

	public List<String> getToolTip() {
		return this.toolTip;
	}

	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
	}
}