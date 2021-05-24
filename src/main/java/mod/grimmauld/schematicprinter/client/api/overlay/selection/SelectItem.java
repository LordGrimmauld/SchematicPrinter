package mod.grimmauld.schematicprinter.client.api.overlay.selection;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	protected static final Minecraft MC = Minecraft.getInstance();
	private final ITextComponent description;
	private final Set<Consumer<RenderGameOverlayEvent.Pre>> renderExtraHooks;

	public SelectItem(ITextComponent description) {
		this.description = description;
		renderExtraHooks = new HashSet<>();
	}

	public void onEnter(SelectOverlay screen) {
	}

	public void onOverlayOpen() {
	}

	public IFormattableTextComponent getDescription() {
		return description.deepCopy().mergeStyle(TextFormatting.WHITE);
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
	}

	public void onRightClick(InputEvent.MouseInputEvent event) {
	}

	public void continuousRendering(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
	}

	@SafeVarargs
	public final SelectItem registerRenderHooks(Consumer<RenderGameOverlayEvent.Pre>... hook) {
		renderExtraHooks.addAll(Arrays.asList(hook));
		return this;
	}

	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		renderExtraHooks.forEach(c -> c.accept(event));
	}
}
