package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BooleanSelectConfig extends NonNullSelectConfig<Boolean> {
	private boolean value;

	public BooleanSelectConfig(ITextComponent description, boolean defaultValue) {
		super(description);
		this.value = defaultValue;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		value = !value;
		this.onValueChanged();
	}

	@Override
	public void onScrolled(int amount) {
		value ^= Math.abs(amount) % 2 != 0;
		this.onValueChanged();
	}

	@Override
	@Nonnull
	protected ITextComponent getState() {
		return new StringTextComponent(value ? "On" : "Off");
	}

	@Override
	public Boolean getValue() {
		return value;
	}
}
