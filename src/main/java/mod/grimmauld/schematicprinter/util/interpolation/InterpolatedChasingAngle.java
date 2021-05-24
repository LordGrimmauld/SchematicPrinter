package mod.grimmauld.schematicprinter.util.interpolation;

import mod.grimmauld.sidebaroverlay.util.AngleHelper;

public class InterpolatedChasingAngle extends InterpolatedChasingValue {
	public InterpolatedChasingAngle() {
	}

	public float get(float partialTicks) {
		return AngleHelper.angleLerp(partialTicks, this.lastValue, this.value);
	}

	protected float getCurrentDiff() {
		return AngleHelper.getShortestAngleDiff(this.value, this.getTarget());
	}
}
