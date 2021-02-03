package mod.grimmauld.schematicprinter.client.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SchematicSelectConfig extends SelectConfig {
	private final List<String> availableSchematics;
	private int index;

	public SchematicSelectConfig(String key, String description) {
		this(key, new TranslationTextComponent(description));
	}

	public SchematicSelectConfig(String key, ITextComponent description) {
		super(key, null, description);
		availableSchematics = new ArrayList<>();
		refreshFiles();
		index = 0;
		SchematicPrinterClient.schematicHandler.setActiveSchematic(getSelectedFile());
		this.onValueChanged();
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

	private void refreshFiles() {
		FileHelper.createFolderIfMissing("schematics");
		availableSchematics.clear();

		try {
			Files.list(Paths.get("schematics/"))
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
		SchematicPrinterClient.schematicHandler.setActiveSchematic(getSelectedFile());
		this.onValueChanged();
	}

	@Nullable
	public String getSelectedFile() {
		if (availableSchematics.isEmpty())
			return null;
		while (index < availableSchematics.size())
			index += availableSchematics.size();
		index %= availableSchematics.size();
		return availableSchematics.get(index);
	}

	@Override
	protected ITextComponent getState() {
		String filename = getSelectedFile();
		return new StringTextComponent(filename != null ? filename : "none");
	}
}
