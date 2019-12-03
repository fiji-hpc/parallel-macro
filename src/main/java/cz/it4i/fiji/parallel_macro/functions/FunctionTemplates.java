
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cz.it4i.fiji.parallel_macro.functions.EarlyEscapeConditions.EarlyEscapeCondition;
import cz.it4i.fiji.parallel_macro.functions.Kernels.ParallelKernel;
import cz.it4i.fiji.parallel_macro.functions.Kernels.SerialKernel;
import mpi.MPI;
import mpi.MPIException;

public class FunctionTemplates {

	private final Logger logger = LoggerFactory.getLogger(
		FunctionTemplates.class);

	public void runWith2DKernelParallel(ParallelKernel kernel,
		EarlyEscapeCondition condition, Object[] parameters)
	{
		try {
			// The first two parameters should always be the input image and the
			// result image:
			String input = (String) parameters[0];
			String result = (String) parameters[1];

			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();

			// Load the input image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			IntBuffer imageBuffer = imageInputOutput.readImage(input);
			int height = imageInputOutput.getHeight();
			int width = imageInputOutput.getWidth();

			if (!condition.escape(parameters)) {
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
						kernel.compute(imageInputOutput, newImagePart, x, y,
							parameters, imageBuffer, rank, workload);
					}
				}

				// Gather the image:
				IntBuffer newImageBuffer = MPI.newIntBuffer(width * height);
				// Gather image parts from all nodes together:
				MPI.COMM_WORLD.gatherv(newImagePart, workload.get("heightParts")[rank] *
					width, MPI.INT, newImageBuffer, workload.get("counts"), workload.get(
						"displacements"), MPI.INT, 0);

				if (rank == 0) {
					imageBuffer = newImageBuffer;
				}
			}
			logger.debug("Rank {} gathered.", rank);

			// Save the result image:
			if (rank == 0) {
				imageInputOutput.writeImage(result, imageBuffer);
				logger.info("Done writing image!");
			}

		}
		catch (MPIException exc) {
			logger.error(exc.getMessage());
		}
	}

	public void runWith2DKernelSerial(SerialKernel kernel,
		EarlyEscapeCondition condition, Object[] parameters)
	{
		String input = (String) parameters[0];
		String result = (String) parameters[1];

		// Read input image:
		ImageInputOutput imageInputOutput = new ImageInputOutput();
		IntBuffer imageBuffer = imageInputOutput.readImage(input);

		int width = imageInputOutput.getWidth();
		int height = imageInputOutput.getHeight();

		if (!condition.escape(parameters)) {
			IntBuffer newImageBuffer = MPI.newIntBuffer(width * height);

			// Process the image:
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					kernel.compute(imageInputOutput, x, y, parameters,
						imageBuffer, newImageBuffer);
				}
			}
			imageBuffer = newImageBuffer;
		}

		// Write output image:
		imageInputOutput.writeImage(result, imageBuffer);
	}

}
