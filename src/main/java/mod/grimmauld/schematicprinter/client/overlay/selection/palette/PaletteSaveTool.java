package mod.grimmauld.schematicprinter.client.overlay.selection.palette;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.gui.StringPromptScreen;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.schematicprinter.util.TextHelper;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.io.IOUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PaletteSaveTool extends SelectItem {
	public PaletteSaveTool(ITextComponent description) {
		super(description);
	}

	public PaletteSaveTool(String description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null)
			return;
		MC.displayGuiScreen(new StringPromptScreen(this::saveAs, SchematicPrinter.MODID + ".screen.save_palette.title"));
	}

	private void saveAs(String filename) {
		TextHelper.sendStatus(MC.player, "palette.saving");
		if (filename.isEmpty()) {
			TextHelper.sendStatus(MC.player, "palette.save_no_name");
			return;
		}
		String folderPath = "palettes";
		FileHelper.createFolderIfMissing(folderPath);
		filename = FileHelper.findFirstValidFilename(filename, folderPath, "nbt");
		String filepath = folderPath + "/" + filename;

		Path path = Paths.get(filepath);
		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
			CompressedStreamTools.writeCompressed(PaletteManager.serialize(), outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				IOUtils.closeQuietly(outputStream);
		}
		TextHelper.sendStatus(MC.player, "palette.saved", filepath);
		PaletteLoadConfig.INSTANCES.forEach(PaletteLoadConfig::refreshFiles);
	}

	@Override
	public boolean shouldRenderPalette() {
		return true;
	}
}
