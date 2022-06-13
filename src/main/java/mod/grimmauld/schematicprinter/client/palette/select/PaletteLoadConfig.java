package mod.grimmauld.schematicprinter.client.palette.select;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PaletteLoadConfig extends SelectConfig<String> {
	private static final Set<PaletteLoadConfig> INSTANCES = new HashSet<>();
	private final List<String> availablePalettes;
	private int index;

	public PaletteLoadConfig(Component description) {
		super(description);
		availablePalettes = new ArrayList<>();
		refreshFiles();
		index = 0;
		this.onValueChanged();
		INSTANCES.add(this);
	}

	public static void refreshAllFiles() {
		INSTANCES.forEach(PaletteLoadConfig::refreshFiles);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		refreshFiles();
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		PaletteManager.loadFromFile(getValue());
	}

	public void refreshFiles() {
		FileHelper.createFolderIfMissing(FileHelper.palettesFilePath);
		availablePalettes.clear();

		try {
			Files.list(Paths.get(FileHelper.palettesFilePath + "/"))
				.filter(f -> !Files.isDirectory(f) && f.getFileName().toString().endsWith(".nbt")).forEach(path -> {
				if (Files.isDirectory(path))
					return;

				availablePalettes.add(path.getFileName().toString());
			});
		} catch (NoSuchFileException e) {
			// No Palettes created yet
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScrolled(int amount) {
		index += amount;
		this.onValueChanged();
	}

	@Nullable
	@Override
	public String getValue() {
		if (availablePalettes.isEmpty())
			return null;
		while (index < availablePalettes.size())
			index += availablePalettes.size();
		index %= availablePalettes.size();
		return availablePalettes.get(index);
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);
		SchematicPrinterClient.paletteOverlay.render(event);
	}
}
