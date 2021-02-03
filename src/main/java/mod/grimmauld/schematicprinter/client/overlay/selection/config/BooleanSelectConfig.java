package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BooleanSelectConfig extends SelectConfig {
	public boolean value;

	public BooleanSelectConfig(String key, ITextComponent description, boolean defaultValue) {
		super(key, null, description);
		this.value = defaultValue;
	}

	public BooleanSelectConfig(String key, String description, boolean defaultValue) {
		this(key, new TranslationTextComponent(description), defaultValue);
	}

	@Override
	public void onScrolled(int amount) {
		value ^= Math.abs(amount) % 2 != 0;
		this.onValueChanged();
	}

	@Override
	protected ITextComponent getState() {
		return new StringTextComponent(value ? "On" : "Off");
	}
}
