package mod.grimmauld.schematicprinter.client.schematics.tools;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.gui.StringPromptScreen;
import mod.grimmauld.schematicprinter.client.schematics.select.SchematicSelectConfig;
import mod.grimmauld.schematicprinter.client.schematics.select.SelectBox;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.schematicprinter.util.TextHelper;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BlockPosSelectConfig;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.gen.feature.template.Template;
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
public class SelectSchematicSave extends SelectBox {
	public SelectSchematicSave(ITextComponent description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null)
			return;
		MC.displayGuiScreen(new StringPromptScreen(this::saveAs, translationComponent("screen.save_schematic.title")));
	}

	private void saveAs(String filename) {
		TextHelper.sendStatus(MC.player, "schematic.saving");
		if (filename.isEmpty()) {
			TextHelper.sendStatus(MC.player, "schematic.save_no_name");
			return;
		}
		if (MC.world == null) {
			TextHelper.sendStatus(MC.player, "schematic.save_no_world");
			return;
		}
		AxisAlignedBB bb = getBoundingBox();
		if (bb == null) {
			TextHelper.sendStatus(MC.player, "schematic.save_empty_selection");
			return;
		}
		BlockPos origin = new BlockPos(bb.minX, bb.minY, bb.minZ);
		BlockPos bounds = new BlockPos(bb.getXSize(), bb.getYSize(), bb.getZSize());
		Template t = new Template();

		t.takeBlocksFromWorld(MC.world, origin, bounds, true, Blocks.AIR);

		FileHelper.createFolderIfMissing(FileHelper.schematicFilePath);
		filename = FileHelper.findFirstValidFilename(filename, FileHelper.schematicFilePath, "nbt");
		String filepath = FileHelper.schematicFilePath + "/" + filename;

		Path path = Paths.get(filepath);
		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
			CompoundNBT nbttagcompound = t.writeToNBT(new CompoundNBT());
			CompressedStreamTools.writeCompressed(nbttagcompound, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				IOUtils.closeQuietly(outputStream);
		}
		TextHelper.sendStatus(MC.player, "schematic.saved", filepath);
		SchematicSelectConfig.refreshAllFiles();
	}
}
