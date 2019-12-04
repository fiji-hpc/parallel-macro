
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cz.it4i.fiji.parallel_macro.functions.EarlyEscapeConditions.EarlyEscapeCondition;
import cz.it4i.fiji.parallel_macro.functions.Kernels.ParallelImagesKernel;
import cz.it4i.fiji.parallel_macro.functions.Kernels.ParallelKernel;
import cz.it4i.fiji.parallel_macro.functions.Kernels.SerialImagesKernel;
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

			// Save the result image:
			if (rank == 0) {
				imageInputOutput.writeImage(result, image);
			}

		}
		catch (MPIException exc) {
			logger.error(exc.getMessage());
		}
	}

	public void runWith2DKernelImagesParallel(ParallelImagesKernel kernel,
		EarlyEscapeCondition condition, Object[] parameters)
	{
		try {
			// The first two parameters should always be the input image and the
			// result image:
			String input = (String) parameters[0];
			String input2 = (String) parameters[1];
			String result = (String) parameters[2];

			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();

			// Load the input image:
			ImageInputOutput imageInputOutput = new ImageInputOutput();
			IntBufferImage image1 = imageInputOutput.readImage(input);
			IntBufferImage image2 = imageInputOutput.readImage(input2);

			// The dimensions of the two input images should always be the same:
			if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2
				.getHeight())
			{
				logger.error("The two input images do not have the same dimensions.");
				return;
			}

			int height = image1.getHeight();
			int width = image1.getWidth();

			if (!condition.escape(parameters)) {
				// Split the workload:
				Workload workload = new Workload(image1, size);

				IntBuffer newImagePart = MPI.newIntBuffer(workload
					.getHeightParts()[rank] * width);

				// Process the image:
				for (int x = 0; x < width; x++) {
					for (int y = workload
						.getDisplacementHeightParts()[rank]; y < (workload
							.getDisplacementHeightParts()[rank] + workload
								.getHeightParts()[rank]); y++)
					{
						kernel.compute(newImagePart, x, y, parameters, image1, image2, rank,
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
					image1.setIntBuffer(newImageBuffer);
				}
			}

			// Save the result image:
			if (rank == 0) {
				imageInputOutput.writeImage(result, image1);
			}

		}
		catch (MPIException exc) {
			logger.error(exc.getMessage());
		}
	}

	public void runWith2DKernelImagesSerial(SerialImagesKernel kernel,
		EarlyEscapeCondition condition, Object[] parameters)
	{
		String input = (String) parameters[0];
		String input2 = (String) parameters[1];
		String result = (String) parameters[2];

		// Read input image:
		ImageInputOutput imageInputOutput = new ImageInputOutput();

		// Two image inputs:
		IntBufferImage imageBuffer = imageInputOutput.readImage(input);
		IntBufferImage imageBuffer2 = imageInputOutput.readImage(input2);

		// The dimensions of the two input images should always be the same:
		if (imageBuffer.getWidth() != imageBuffer2.getWidth() || imageBuffer
			.getHeight() != imageBuffer2.getHeight())
		{
			logger.error("The two input images do not have the same dimensions.");
			return;
		}

		int width = imageBuffer.getWidth();
		int height = imageBuffer.getHeight();

		if (!condition.escape(parameters)) {
			IntBuffer newImageBuffer = MPI.newIntBuffer(width * height);

			// Process the image:
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					kernel.compute(x, y, parameters, imageBuffer, imageBuffer2,
						newImageBuffer);
				}
			}
			imageBuffer.setIntBuffer(newImageBuffer);
		}

		// Write output image:
		imageInputOutput.writeImage(result, imageBuffer);
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
