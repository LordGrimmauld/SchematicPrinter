package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.selection.IOverlayEventListener;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SelectConfig extends SelectItem {
	public final String key;

	public SelectConfig(String key, @Nullable IOverlayEventListener listener, ITextComponent description) {
		super(description, listener);
		this.key = key;
	}

	public abstract void onScrolled(int amount);

	@Override
	public IFormattableTextComponent getDescription() {
		return super.getDescription().appendString(": ").appendString(this.getState());
	}

	protected abstract String getState();
}
