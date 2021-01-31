package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BooleanSelectConfig extends SelectConfig {
	private boolean value;

	public BooleanSelectConfig(ITextComponent description, boolean defaultValue) {
		super(null, description);
		this.value = defaultValue;
	}

	public BooleanSelectConfig(String description, boolean defaultValue) {
		this(new TranslationTextComponent(description), defaultValue);
	}

	@Override
	public void onScrolled(int amount) {
		value ^= Math.abs(amount) % 2 != 0;
	}

	@Override
	protected String getState() {
		return value ? "On" : "Off";
	}
}
