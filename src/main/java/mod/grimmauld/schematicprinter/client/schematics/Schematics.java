package mod.grimmauld.schematicprinter.client.schematics;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.io.IOUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Schematics {
	public static Optional<StructureTemplate> loadSchematic(String schematic) {
		InputStream stream = null;

		try {
			stream = Files.newInputStream(Paths.get(FileHelper.schematicFilePath + "/" + schematic), StandardOpenOption.READ);
			CompoundTag nbt = NbtIo.readCompressed(stream);
			StructureTemplate t = new StructureTemplate();
			t.load(nbt);
			return Optional.of(t);
		} catch (IOException ignored) {
		} finally {
			if (stream != null) {
				IOUtils.closeQuietly(stream);
			}
		}

		return Optional.empty();
	}
}
