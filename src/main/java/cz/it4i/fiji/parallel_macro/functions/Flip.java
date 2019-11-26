
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
import java.util.Map;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Flip implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		// Get input:
		String inputString = (String) parameters[0];
		String resultString = (String) parameters[1];
		double flipXString = (double) parameters[2];
		double flipYString = (double) parameters[3];

		// Convert input to proper types:
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
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				parallelFlip(inputString, resultString, flipXBoolean, flipYBoolean);
			}
			else {
				serialFlip(inputString, resultString, flipXBoolean, flipYBoolean);
			}
		}
		catch (MPIException e) {
			e.printStackTrace();
		}
	}

	private void parallelFlip(String input, String result, boolean flipX,
		boolean flipY)
	{
		try {
			// Get rank and size of nodes:
			int rank = MPI.COMM_WORLD.getRank();
			int size = MPI.COMM_WORLD.getSize();

			System.out.println("Trying to flip the image.");

			// Read the selected image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			IntBuffer imagePixels = imageInputOutput.readImage(input);
			int width = imageInputOutput.getWidth();
			int height = imageInputOutput.getHeight();

			IntBuffer newImagePixels = MPI.newIntBuffer(width * height);
			System.out.println("Rank " + rank + ". Done reading image!");

			if (flipY || flipX) {
				Map<String, int[]> workload = imageInputOutput.splitWorkLoad(size);

				// Create smaller local array of the image:
				IntBuffer flippedPixels = MPI.newIntBuffer(width * workload.get(
					"heightParts")[rank]);

				// Each node should flip its image part:
				for (int x = 0; x < width; x++) {
					for (int y = workload.get(
						"displacementHeightParts")[rank]; y < (workload.get(
							"displacementHeightParts")[rank] + workload.get(
								"heightParts")[rank]); y++)
					{
						imageInputOutput.setValueAt(flippedPixels, width, x, (y - workload
							.get("displacementHeightParts")[rank]), imageInputOutput
								.getValueAt(imagePixels, width, (flipX) ? (width - 1 - x) : x,
									flipY ? (height - 1) - y : y));
					}
				}

				// Gather image parts from all nodes together:
				MPI.COMM_WORLD.gatherv(flippedPixels, workload.get(
					"heightParts")[rank] * width, MPI.INT, newImagePixels, workload.get(
						"counts"), workload.get("displacements"), MPI.INT, 0);
				System.out.println("Gathered! " + rank);
			}
			else {
				newImagePixels = imagePixels;
			}

			MPI.COMM_WORLD.barrier();
			// Write the selected image:
			if (rank == 0) {
				imageInputOutput.writeImage(result, newImagePixels);
				System.out.println("Done writing image!");
			}
		}
		catch (MPIException e) {
			e.printStackTrace();
		}
	}

	// Do not remove this version. Keep this serial version for testing.
	private void serialFlip(String input, String result, boolean flipX,
		boolean flipY)
	{
		ImageInputOutput imageInputOutput = new ImageInputOutput();

		System.out.println("Trying to flip the image.");

		// Read the selected image:
		IntBuffer imagePixels = imageInputOutput.readImage(input);
		System.out.println("Done reading image!");

		int width = imageInputOutput.getWidth();
		int height = imageInputOutput.getHeight();

		if (flipX || flipY) {
			IntBuffer flippedPixels = MPI.newIntBuffer(width * height);

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					imageInputOutput.setValueAt(flippedPixels, width, x, y,
						imageInputOutput.getValueAt(imagePixels, width, (flipX) ? ((width -
							1) - x) : x, (flipX) ? y : ((height - 1) - y)));
				}
			}
			imagePixels = flippedPixels;
		}

		// Write the selected image:
		imageInputOutput.writeImage(result, imagePixels);
		System.out.println("Done writing image!");
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
		return "input image path, result image path, flipX, flipY";
	}
}
