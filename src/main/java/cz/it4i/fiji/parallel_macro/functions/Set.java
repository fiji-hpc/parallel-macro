
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Set implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		String input = (String) parameters[0];
		String result = (String) parameters[1];
		int value = (int) ((double) parameters[2]);

		// Call the actual function:
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				parallelSet(input, result, value);
			}
			else {
				serialSet(input, result, value);
			}
		}
		catch (MPIException e) {
			e.printStackTrace();
		}
	}

	private void parallelSet(String input, String result, int value) {
		// Set the parameters, namely the value to set all pixels to:
		FunctionTemplate2D template = new FunctionTemplate2D();
		List<Object> parameters = new ArrayList<>();
		parameters.add(value);

		template.runWithKernel(input, result, Kernels.set, parameters);
	}

	private void serialSet(String input, String result, int value) {
		// Read input image:
		ImageInputOutput imageInputOutput = new ImageInputOutput();
		imageInputOutput.readImage(input);

		int width = imageInputOutput.getWidth();
		int height = imageInputOutput.getHeight();

		IntBuffer newImage = MPI.newIntBuffer(width * height);

		// Process the image:
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				imageInputOutput.setValueAt(newImage, width, x, y, new Color(value,
					value, value));
			}
		}

		// Write output image:
		imageInputOutput.writeImage(result, newImage);
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Sets all pixels to the given value.";
	}

	@Override
	public String parameters() {
		return "input image path, result image path, value";
	}

}
