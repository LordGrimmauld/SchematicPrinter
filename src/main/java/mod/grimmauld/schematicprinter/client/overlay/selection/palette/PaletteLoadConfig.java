package mod.grimmauld.schematicprinter.client.overlay.selection.palette;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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
public class PaletteLoadConfig extends SelectConfig {
	public static final Set<PaletteLoadConfig> INSTANCES = new HashSet<>();
	private final List<String> availablePalettes;
	private int index;

	public PaletteLoadConfig(ITextComponent description) {
		super(description);
		availablePalettes = new ArrayList<>();
		refreshFiles();
		index = 0;
		this.onValueChanged();
		INSTANCES.add(this);
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		refreshFiles();
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		PaletteManager.loadFromFile(getSelectedFile());
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
	public String getSelectedFile() {
		if (availablePalettes.isEmpty())
			return null;
		while (index < availablePalettes.size())
			index += availablePalettes.size();
		index %= availablePalettes.size();
		return availablePalettes.get(index);
	}

	@Override
	protected ITextComponent getState() {
		String filename = getSelectedFile();
		return new StringTextComponent(filename != null ? filename : "none");
	}

	@Override
	public boolean shouldRenderPalette() {
		return true;
	}
}
