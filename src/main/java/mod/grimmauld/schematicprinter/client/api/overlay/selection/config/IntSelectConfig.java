package mod.grimmauld.schematicprinter.client.api.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IntSelectConfig extends NonNullSelectConfig<Integer> {
	private final int min;
	private final int max;
	private int value;

	public IntSelectConfig(ITextComponent description, int min, int defaultValue, int max) {
		super(description);
		this.min = min;
		this.value = defaultValue;
		this.max = max;
		this.onValueChanged();
	}

	@Override
	public void onScrolled(int amount) {
		value = MathHelper.clamp(value + amount, min, max);
		this.onValueChanged();
	}

	@Override
	public Integer getValue() {
		return value;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
