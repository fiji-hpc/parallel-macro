
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
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
			IntBufferImage image = imageInputOutput.readImage(input);
			int height = image.getHeight();
			int width = image.getWidth();

			if (!condition.escape(parameters)) {
				// Split the workload:
				Workload workload = new Workload(image, size);

				IntBuffer newImagePart = MPI.newIntBuffer(workload
					.getHeightParts()[rank] * width);

				// Process the image:
				for (int x = 0; x < width; x++) {
					for (int y = workload
						.getDisplacementHeightParts()[rank]; y < (workload
							.getDisplacementHeightParts()[rank] + workload
								.getHeightParts()[rank]); y++)
					{
						kernel.compute(newImagePart, x, y, parameters, image, rank,
							workload);
					}
				}

				// Gather the image:
				IntBuffer newImageBuffer = MPI.newIntBuffer(width * height);
				// Gather image parts from all nodes together:
				MPI.COMM_WORLD.gatherv(newImagePart, workload.getHeightParts()[rank] *
					width, MPI.INT, newImageBuffer, workload.getCounts(), workload
						.getDisplacements(), MPI.INT, 0);

				if (rank == 0) {
					image.setIntBuffer(newImageBuffer);
				}
			}
			logger.debug("Rank {} gathered.", rank);

			// Save the result image:
			if (rank == 0) {
				imageInputOutput.writeImage(result, image);
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
		IntBufferImage imageBuffer = imageInputOutput.readImage(input);

		int width = imageBuffer.getWidth();
		int height = imageBuffer.getHeight();

		if (!condition.escape(parameters)) {
			IntBuffer newImageBuffer = MPI.newIntBuffer(width * height);

			// Process the image:
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					kernel.compute(x, y, parameters, imageBuffer, newImageBuffer);
				}
			}
			imageBuffer.setIntBuffer(newImageBuffer);
		}

		// Write output image:
		imageInputOutput.writeImage(result, imageBuffer);
	}
}
