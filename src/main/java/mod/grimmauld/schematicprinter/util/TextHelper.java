package mod.grimmauld.schematicprinter.util;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TextHelper {
	public static void sendStatus(@Nullable PlayerEntity player, String key, Object... args) {
		if (player != null)
			player.sendStatusMessage(new TranslationTextComponent(SchematicPrinter.MODID + "." + key, args), true);
	}
}
