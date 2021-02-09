package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SelectConfig<T extends Comparable<? super T>> extends SelectItem {
	private final Set<Consumer<SelectConfig<? extends T>>> onChangedListeners = new HashSet<>();

	public SelectConfig(ITextComponent description) {
		super(description);
	}

	public abstract void onScrolled(int amount);

	@Override
	public ITextComponent getDescription() {
		return super.getDescription().appendText(": ").appendSibling(this.getState());
	}

	protected ITextComponent getState() {
		T value = getValue();
		return new StringTextComponent(value == null ? "none" : value.toString());
	}

	protected void onValueChanged() {
		onChangedListeners.forEach(process -> process.accept(this));
	}

	public void registerChangeListener(Consumer<SelectConfig<? extends T>> listener) {
		onChangedListeners.add(listener);
	}

	@Nullable
	public abstract T getValue();
}
