
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.file.Path;
import java.nio.file.Paths;

import ij.macro.MacroExtension;

public class Flip implements MyMacroExtensionDescriptor {

	private void flip(Path input, Path result, boolean flipX, boolean flipY) {
		System.out.println(
			"Will attempt to flip the image with given parameters: \n" + "input: " +
				input.toString() + " result: " + result + " flipX: " + flipX +
				" flipY: " + flipY);
	}

	@Override
	public void runFromMacro(Object[] parameters) {
		// Get input:
		String inputString = (String) parameters[0];
		String resultString = (String) parameters[1];
		double flipXString = (double) parameters[2];
		double flipYString = (double) parameters[3];

		// Convert input to proper types:
		Path inputPath = Paths.get(inputString);
		Path resultPath = Paths.get(resultString);
		boolean flipXBoolean = false;
		boolean flipYBoolean = false;

		// Macro always uses double for numbers and booleans:
		if (flipXString == 1.0) {
			flipXBoolean = true;
		}
		if (flipYString == 1.0) {
			flipYBoolean = true;
		}

		// Call the actual function:
		flip(inputPath, resultPath, flipXBoolean, flipYBoolean);
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER, MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Flips an image in the dimension or dimensions specified.";
	}

	@Override
	public String parameters() {
		return "input, result, flipX, flipY";
	}

}
