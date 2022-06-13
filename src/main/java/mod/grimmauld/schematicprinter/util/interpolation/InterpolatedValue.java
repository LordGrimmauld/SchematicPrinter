package mod.grimmauld.schematicprinter.util.interpolation;

import net.minecraft.util.Mth;

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
		return Mth.lerp(partialTicks, this.lastValue, this.value);
	}
}
