package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IntSelectConfig extends SelectConfig {
	public final int min;
	public final int max;
	public int value;

	public IntSelectConfig(String key, ITextComponent description, int min, int defaultValue, int max) {
		super(key, null, description);
		this.min = min;
		this.value = defaultValue;
		this.max = max;
		this.onValueChanged();
	}

	public IntSelectConfig(String key, String description, int min, int defaultValue, int max) {
		this(key, new TranslationTextComponent(description), min, defaultValue, max);
	}

	@Override
	public void onScrolled(int amount) {
		value = MathHelper.clamp(value + amount, min, max);
		this.onValueChanged();
	}

	@Override
	protected ITextComponent getState() {
		return new StringTextComponent(String.valueOf(value));
	}
}
