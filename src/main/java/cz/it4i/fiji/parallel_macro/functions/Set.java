
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.Map;

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
		System.out.println("Serial set");
		set(input, result, value);

		System.out.println("Parallel set");
		parallelSet(input, result, value);
	}

	private void parallelSet(String input, String result, int value) {
		try {
			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();

			// Load the input image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			imageInputOutput.readImage(input);
			int height = imageInputOutput.getHeight();
			int width = imageInputOutput.getWidth();

			// Split the workload:
			Map<String, int[]> splitWorkload = imageInputOutput.splitWorkLoad(size);

			IntBuffer newImagePart = MPI.newIntBuffer(splitWorkload.get(
				"heightParts")[rank] * width);

			// Process the image:
			for (int x = 0; x < width; x++) {
				for (int y = splitWorkload.get(
					"displacementHeightParts")[rank]; y < (splitWorkload.get(
						"displacementHeightParts")[rank] + splitWorkload.get(
							"heightParts")[rank]); y++)
				{
					imageInputOutput.setValueAt(newImagePart, width, x, (y - splitWorkload
						.get("displacementHeightParts")[rank]), new Color(value, value,
							value));
				}
			}

			// Gather the image:
			IntBuffer newImage = MPI.newIntBuffer(width * height);
			// Gather image parts from all nodes together:
			MPI.COMM_WORLD.gatherv(newImagePart, splitWorkload.get(
				"heightParts")[rank] * width, MPI.INT, newImage, splitWorkload.get(
					"counts"), splitWorkload.get("displacements"), MPI.INT, 0);
			System.out.println("Gathered! " + rank);

			// Save the result image:
			imageInputOutput.writeImage(result, newImage);

		}
		catch (MPIException exc) {
			exc.printStackTrace();
		}
	}

	private void set(String input, String result, int value) {
		// Read input image:
		ImageInputOutput imageInputOutput = new ImageInputOutput();
		imageInputOutput.readImage(input);

		int width = imageInputOutput.getWidth();
		int height = imageInputOutput.getHeight();

		IntBuffer newImage = MPI.newIntBuffer(width * height);

		// Process the image:
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				imageInputOutput.setValueAt(newImage, width, x, y, value);
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
