package mod.grimmauld.schematicprinter.util;

import mod.grimmauld.schematicprinter.SchematicPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
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
}
