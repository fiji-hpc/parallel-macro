
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

import mpi.MPI;
import mpi.MPIException;

public class FunctionTemplate2D {

	public void runWithKernel(String input, String result, Kernel kernel,
		List<Object> parameters)
	{
		try {
			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();

			// Load the input image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			IntBuffer imagePixels = imageInputOutput.readImage(input);
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
					kernel.compute(imageInputOutput, newImagePart, width, x, y,
						parameters, imagePixels, rank, workload);
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
			if (rank == 0) {
				imageInputOutput.writeImage(result, newImage);
				System.out.println("Done writing image!");
			}

		}
		catch (MPIException exc) {
			exc.printStackTrace();
		}
	}

}
