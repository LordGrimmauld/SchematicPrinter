package mod.grimmauld.schematicprinter.util.interpolation;

import net.minecraft.util.math.MathHelper;

public class InterpolatedValue {
	public float value = 0.0F;
	public float lastValue = 0.0F;

	public InterpolatedValue() {
	}

	public void set(float value) {
		this.lastValue = this.value;
		this.value = value;
	}

	public float get(float partialTicks) {
		return MathHelper.lerp(partialTicks, this.lastValue, this.value);
	}
}
