package mod.grimmauld.schematicprinter.util;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public enum EmptyModelData implements IModelData {
	INSTANCE;

	private EmptyModelData() {
	}

	public boolean hasProperty(ModelProperty<?> prop) {
		return false;
	}

	public <T> T getData(ModelProperty<T> prop) {
		return null;
	}

	public <T> T setData(ModelProperty<T> prop, T data) {
		return null;
	}
}
