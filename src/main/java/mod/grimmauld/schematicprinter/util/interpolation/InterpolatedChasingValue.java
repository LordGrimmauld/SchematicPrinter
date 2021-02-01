package mod.grimmauld.schematicprinter.util.interpolation;

public class InterpolatedChasingValue extends InterpolatedValue {
	final float speed = 0.5F;
	final float eps = 2.4414062E-4F;
	float target = 0.0F;

	public InterpolatedChasingValue() {
	}

	public void tick() {
		float diff = this.getCurrentDiff();
		if (!(Math.abs(diff) < this.eps)) {
			this.set(this.value + diff * this.speed);
		}
	}

	protected float getCurrentDiff() {
		return this.getTarget() - this.value;
	}

	public void setStart(float value) {
		this.lastValue = this.value = value;
		this.setTarget(value);
	}

	public float getTarget() {
		return this.target;
	}

	public void setTarget(float target) {
		this.target = target;
	}
}
