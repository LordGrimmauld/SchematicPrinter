package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	private final ITextComponent description;
	protected Consumer<SelectOverlay> onInvoke;

	public SelectItem(ITextComponent description, @Nullable Consumer<SelectOverlay> onInvoke) {
		this.onInvoke = onInvoke;
		this.description = description;
	}

	public SelectItem(String description, @Nullable Consumer<SelectOverlay> onInvoke) {
		this(new TranslationTextComponent(description), onInvoke);
	}

	public void invoke(SelectOverlay screen) {
		if (onInvoke != null)
			onInvoke.accept(screen);
	}

	public void onOverlayOpen() {
	}

	public ITextComponent getDescription() {
		return description.deepCopy();
	}
}
