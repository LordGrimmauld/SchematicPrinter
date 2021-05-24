package mod.grimmauld.schematicprinter.client.api.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.api.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.util.text.ITextComponent;

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

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SchematicSelectConfig extends SelectConfig<String> {
	private static final Set<SchematicSelectConfig> INSTANCES = new HashSet<>();
	private final List<String> availableSchematics;
	private int index;

	public SchematicSelectConfig(ITextComponent description) {
		super(description);
		availableSchematics = new ArrayList<>();
		refreshFiles();
		index = 0;
		SchematicPrinterClient.schematicHandler.setActiveSchematic(getValue());
		this.onValueChanged();
		INSTANCES.add(this);
	}

	public static void refreshAllFiles() {
		INSTANCES.forEach(SchematicSelectConfig::refreshFiles);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		refreshFiles();
	}

	@Override
	public void onOverlayOpen() {
		super.onOverlayOpen();
		refreshFiles();
	}

	public void refreshFiles() {
		FileHelper.createFolderIfMissing(FileHelper.schematicFilePath);
		availableSchematics.clear();

		try {
			Files.list(Paths.get(FileHelper.schematicFilePath + "/"))
				.filter(f -> !Files.isDirectory(f) && f.getFileName().toString().endsWith(".nbt")).forEach(path -> {
				if (Files.isDirectory(path))
					return;

				availableSchematics.add(path.getFileName().toString());
			});
		} catch (NoSuchFileException e) {
			// No Schematics created yet
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onScrolled(int amount) {
		index += amount;
		SchematicPrinterClient.schematicHandler.setActiveSchematic(getValue());
		this.onValueChanged();
	}

	@Nullable
	@Override
	public String getValue() {
		if (availableSchematics.isEmpty())
			return null;
		while (index < availableSchematics.size())
			index += availableSchematics.size();
		index %= availableSchematics.size();
		return availableSchematics.get(index);
	}
}
