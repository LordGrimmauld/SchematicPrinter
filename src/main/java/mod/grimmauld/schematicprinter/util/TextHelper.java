package mod.grimmauld.schematicprinter.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TextHelper {
	public static void sendStatus(@Nullable Player player, String key, Object... args) {
		if (player != null)
			player.displayClientMessage(new TranslatableComponent(translationKey(key), args), true);
	}

	public static String translationKey(String key) {
		return SchematicPrinter.MODID + "." + key;
	}

	public static Component translationComponent(String key) {
		return new TranslatableComponent(translationKey(key));
	}
}
