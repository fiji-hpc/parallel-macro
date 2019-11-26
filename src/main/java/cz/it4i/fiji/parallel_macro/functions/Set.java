
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
		try {
			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();

			// Load the input image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			imageInputOutput.readImage(input);
			int height = imageInputOutput.getHeight();
			int width = imageInputOutput.getWidth();

			// Split the workload:
			Map<String, int[]> workload = imageInputOutput.splitWorkLoad(size);

			IntBuffer newImagePart = MPI.newIntBuffer(workload.get(
				"heightParts")[rank] * width);

			// Process the image:
			for (int x = 0; x < width; x++) {
				for (int y = workload.get(
					"displacementHeightParts")[rank]; y < (workload.get(
						"displacementHeightParts")[rank] + workload.get(
							"heightParts")[rank]); y++)
				{
					imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
						"displacementHeightParts")[rank]), new Color(value, value, value));
				}
			}

			// Gather the image:
			IntBuffer newImage = MPI.newIntBuffer(width * height);
			// Gather image parts from all nodes together:
			MPI.COMM_WORLD.gatherv(newImagePart, workload.get("heightParts")[rank] *
				width, MPI.INT, newImage, workload.get("counts"), workload.get(
					"displacements"), MPI.INT, 0);

			System.out.println("Gathered! " + rank);

			// Save the result image:
			imageInputOutput.writeImage(result, newImage);

		}
		catch (MPIException exc) {
			exc.printStackTrace();
		}
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
