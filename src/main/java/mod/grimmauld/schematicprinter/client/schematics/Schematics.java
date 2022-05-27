package mod.grimmauld.schematicprinter.client.schematics;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.gen.feature.template.Template;
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
	public static Optional<Template> loadSchematic(String schematic) {
		InputStream stream = null;

		try {
			stream = Files.newInputStream(Paths.get(FileHelper.schematicFilePath + "/" + schematic), StandardOpenOption.READ);
			CompoundNBT nbt = CompressedStreamTools.readCompressed(stream);
			Template t = new Template();
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
