package mod.grimmauld.schematicprinter.client.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.client.gui.StringPromptScreen;
import mod.grimmauld.schematicprinter.client.overlay.SelectOverlay;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.BlockPosSelectConfig;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.schematicprinter.util.TextHelper;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.Template;
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
public class SelectSchematicSave extends SelectBox {
	public SelectSchematicSave(String description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null)
			return;
		MC.displayGuiScreen(new StringPromptScreen(this::saveAs, SchematicPrinter.MODID + ".screen.save_schematic.title"));
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
		bb = bb.expand(1, 1, 1);
		BlockPos origin = new BlockPos(bb.minX, bb.minY, bb.minZ);
		BlockPos bounds = new BlockPos(bb.getXSize(), bb.getYSize(), bb.getZSize());
		Template t = new Template();

		t.takeBlocksFromWorld(MC.world, origin, bounds, true, Blocks.AIR);

		String folderPath = "schematics";
		FileHelper.createFolderIfMissing(folderPath);
		filename = FileHelper.findFirstValidFilename(filename, folderPath, "nbt");
		String filepath = folderPath + "/" + filename;

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
	}
}
