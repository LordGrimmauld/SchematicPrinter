package mod.grimmauld.schematicprinter.client.schematics.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.gui.StringPromptScreen;
import mod.grimmauld.schematicprinter.client.schematics.select.SchematicSelectConfig;
import mod.grimmauld.schematicprinter.client.schematics.select.SelectBox;
import mod.grimmauld.schematicprinter.util.FileHelper;
import mod.grimmauld.schematicprinter.util.TextHelper;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BlockPosSelectConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
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
	public SelectSchematicSave(Component description, BlockPosSelectConfig pos1, BlockPosSelectConfig pos2) {
		super(description, pos1, pos2);
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		if (MC.player == null)
			return;
		MC.setScreen(new StringPromptScreen(this::saveAs, translationComponent("screen.save_schematic.title")));
	}

	private void saveAs(String filename) {
		TextHelper.sendStatus(MC.player, "schematic.saving");
		if (filename.isEmpty()) {
			TextHelper.sendStatus(MC.player, "schematic.save_no_name");
			return;
		}
		if (MC.level == null) {
			TextHelper.sendStatus(MC.player, "schematic.save_no_world");
			return;
		}
		AABB bb = getBoundingBox();
		if (bb == null) {
			TextHelper.sendStatus(MC.player, "schematic.save_empty_selection");
			return;
		}
		BlockPos origin = new BlockPos(bb.minX, bb.minY, bb.minZ);
		BlockPos bounds = new BlockPos(bb.getXsize(), bb.getYsize(), bb.getZsize());
		StructureTemplate t = new StructureTemplate();

		t.fillFromWorld(MC.level, origin, bounds, true, Blocks.AIR);

		FileHelper.createFolderIfMissing(FileHelper.schematicFilePath);
		filename = FileHelper.findFirstValidFilename(filename, FileHelper.schematicFilePath, "nbt");
		String filepath = FileHelper.schematicFilePath + "/" + filename;

		Path path = Paths.get(filepath);
		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
			CompoundTag nbttagcompound = t.save(new CompoundTag());
			NbtIo.writeCompressed(nbttagcompound, outputStream);
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
