package mod.grimmauld.schematicprinter.client.overlay.selection;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	protected static final Minecraft MC = Minecraft.getInstance();
	private final ITextComponent description;

	public SelectItem(ITextComponent description) {
		this.description = description;
	}

	public SelectItem(String description) {
		this(new TranslationTextComponent(description));
	}

	public void onEnter(SelectOverlay screen) {
	}

	public void onOverlayOpen() {
	}

	public ITextComponent getDescription() {
		return description.deepCopy();
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
	}

	public void onRightClick(InputEvent.MouseInputEvent event) {
	}

	public void continuousRendering(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	public boolean shouldRenderPalette() {
		return false;
	}
}
