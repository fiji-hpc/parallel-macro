
package cz.it4i.fiji.parallel_macro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroFunctionWrapper {

	Logger logger = LoggerFactory.getLogger(MacroFunctionWrapper.class);

	private Path getCurrentPath() {
		String filePathString = getClass().getProtectionDomain().getCodeSource()
			.getLocation().getPath();
		logger.info("Parallel Macro is located in {}.", filePathString);
		File jarDir = new File(filePathString);
		String pathString = jarDir.getAbsolutePath();
		// Get parent to exclude the jar filename from the path:
		return Paths.get(pathString).getParent();
	}

	private String getLibraryFilePathString(Path currentPath) {
		currentPath = currentPath.getParent();
		String pathString = currentPath.toFile().getAbsolutePath();
		return pathString + "/macros/Library.txt";
	}

	private boolean fileExists(File file) {
		return file.exists() && !file.isDirectory();
	}

	private boolean functionSectionExists(File file) {
		boolean found = false;
		String line;
		String key = "// ParallelMacro start";
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
			file)))
		{
			while (((line = bufferedReader.readLine()) != null) && !found) {
				if (line.contains(key)) {
					found = true;
				}
			}
		}
		catch (IOException exception) {
			logger.error("Failed to read Library.txt file. {} ", exception);
		}
		return found;
	}

	private void appendSection(String functionSection, File file) {
		String header = "\n// ParallelMacro start\n";
		String footer = "\n// ParallelMacro end\n";
		try {
			Files.write(file.toPath(), header.getBytes(), StandardOpenOption.APPEND);
			Files.write(file.toPath(), functionSection.getBytes(),
				StandardOpenOption.APPEND);
			Files.write(file.toPath(), footer.getBytes(), StandardOpenOption.APPEND);
		}
		catch (IOException exception) {
			logger.error("Failed to append the section in file. {} ", exception);
		}
	}

	private boolean createFile(File file) {
		boolean fileCreated = false;
		try {
			fileCreated = file.createNewFile();
		}
		catch (IOException exception) {
			logger.error("Unable to create Library.txt in Fiji's macro directory {} ",
				exception.getCause());
		}
		return fileCreated;
	}

//	private boolean sectionIsDifferent(String functionSection, File file) {
//		return false;
//	}
//
//	private void removeFunctionSection(File file) {
//
//	}

	private String readFunctionSection() {
		String text = "";
		String line;
		try (BufferedReader resourceReader = new BufferedReader(
			new InputStreamReader(this.getClass().getClassLoader()
				.getResourceAsStream("function.ijm"))))
		{
			while ((line = resourceReader.readLine()) != null) {
				text = text.concat(line).concat("\n");
			}
		}
		catch (IOException exception) {
			logger.error("Could not read function.ijm from resources. {} ", exception
				.getCause());
		}
		return text;
	}

	public void wrapFunctions() {
		Path currentPath = getCurrentPath();
		String filePathString = getLibraryFilePathString(currentPath);
		File fijiLibraryFile = new File(filePathString);
		String functionSection = readFunctionSection();
		if (!fileExists(fijiLibraryFile)) {
			createFile(fijiLibraryFile);
			appendSection(functionSection, fijiLibraryFile);
		}
		else {
			if (!functionSectionExists(fijiLibraryFile)) {
				appendSection(functionSection, fijiLibraryFile);
			}
//			else if (sectionIsDifferent(functionSection, fijiLibraryFile)) {
//				removeFunctionSection(fijiLibraryFile);
//				appendSection(functionSection, fijiLibraryFile);
//			}
		}
	}

}
