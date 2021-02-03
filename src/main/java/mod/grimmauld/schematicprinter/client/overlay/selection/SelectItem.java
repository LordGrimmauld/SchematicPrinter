package mod.grimmauld.schematicprinter.client.overlay.selection;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	protected static final Minecraft MC = Minecraft.getInstance();
	@Nullable
	public final IOverlayEventListener listener;
	private final ITextComponent description;

	public SelectItem(ITextComponent description, @Nullable IOverlayEventListener listener) {
		this.listener = listener;
		this.description = description;
	}

	public SelectItem(String description, @Nullable IOverlayEventListener listener) {
		this(new TranslationTextComponent(description), listener);
	}

	public void onEnter(SelectOverlay screen) {
		if (listener != null)
			listener.onEnter(screen);
	}

	public void onOverlayOpen() {
		if (listener != null)
			listener.init();
	}

	public IFormattableTextComponent getDescription() {
		return description.deepCopy();
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
		if (listener != null)
			listener.onScroll(event);
	}

	public void onRightClick(InputEvent.MouseInputEvent event) {
		if (listener != null)
			listener.onRightClick(event);
	}

	public void continuousRendering(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}
}
