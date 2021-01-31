package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SelectConfig extends SelectItem {
	public SelectConfig(@Nullable Consumer<SelectOverlay> onInvoke, ITextComponent description) {
		super(description, onInvoke);
	}

	public abstract void onScrolled(int amount);

	@Override
	public ITextComponent getDescription() {
		return super.getDescription().appendText(": ").appendText(this.getState());
	}

	protected abstract String getState();
}
