package mod.grimmauld.schematicprinter.client.palette.select;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.gui.StringPromptScreen;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.schematicprinter.util.TextHelper;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.apache.commons.io.IOUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static mod.grimmauld.schematicprinter.util.TextHelper.translationComponent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PaletteSaveTool extends SelectItem {
	public PaletteSaveTool(ITextComponent description) {
		super(description);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null)
			return;
		MC.displayGuiScreen(new StringPromptScreen(this::saveAs, translationComponent("screen.save_palette.title")));
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
		PaletteLoadConfig.refreshAllFiles();
	}

	@Override
	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		super.renderExtra(event);
		SchematicPrinterClient.paletteOverlay.render(event);
	}
}
