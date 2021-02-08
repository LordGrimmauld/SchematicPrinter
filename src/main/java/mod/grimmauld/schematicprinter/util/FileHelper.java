package mod.grimmauld.schematicprinter.util;

import mod.grimmauld.schematicprinter.SchematicPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class FileHelper {
	public static final String schematicFilePath = "schematics";
	public static final String palettesFilePath = "palettes";

	public static void createFolderIfMissing(String name) {
		Path path = Paths.get(name);
		if (path.getParent() != null)
			createFolderIfMissing(path.getParent()
				.toString());

		if (!Files.isDirectory(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				SchematicPrinter.LOGGER.warn("Could not create Folder: " + name);
			}
		}
	}

	public static String findFirstValidFilename(String name, String folderPath, String extension) {
		int index = 0;

		String filename;
		String filepath;
		do {
			filename = slug(name) + (index == 0 ? "" : "_" + index) + "." + extension;
			++index;
			filepath = folderPath + "/" + filename;
		} while (Files.exists(Paths.get(filepath)));

		return filename;
	}

	public static String slug(String name) {
		return name.toLowerCase(Locale.ENGLISH).replace(' ', '_').replace('!', '_').replace(':', '_').replace('?', '_');
	}

}
