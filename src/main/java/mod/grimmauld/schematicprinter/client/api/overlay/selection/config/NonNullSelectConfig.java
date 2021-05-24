package mod.grimmauld.schematicprinter.client.api.overlay.selection.config;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public abstract class NonNullSelectConfig<T extends Comparable<? super T>> extends SelectConfig<T> {
	public NonNullSelectConfig(ITextComponent description) {
		super(description);
	}

	@Override
	@Nonnull
	public abstract T getValue();
}
