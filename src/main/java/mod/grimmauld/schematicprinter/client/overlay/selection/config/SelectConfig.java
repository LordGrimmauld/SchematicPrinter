package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SelectConfig extends SelectItem {
	private final Set<Consumer<SelectConfig>> onChangedListeners = new HashSet<>();

	public SelectConfig(ITextComponent description) {
		super(description);
	}

	public abstract void onScrolled(int amount);

	@Override
	public IFormattableTextComponent getDescription() {
		return super.getDescription().appendString(": ").append(this.getState());
	}

	protected abstract ITextComponent getState();

	protected void onValueChanged() {
		onChangedListeners.forEach(process -> process.accept(this));
	}

	public void registerChangeListener(Consumer<SelectConfig> listener) {
		onChangedListeners.add(listener);
	}
}
