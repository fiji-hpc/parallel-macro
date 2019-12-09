
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.macro.MacroExtension;
import ij.process.ImageProcessor;
import mpi.MPI;
import mpi.MPIException;

public class GaussianBlur implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Flip.class);

	@Override
	public void runFromMacro(Object[] parameters) {
		String input = (String) parameters[0];
		String result = (String) parameters[1];
		double sigma = (double) parameters[2];

		gaussianBlur(input, result, sigma);
	}

	private void gaussianBlur(String input, String result, double sigma) {
		try {

			int size = MPI.COMM_WORLD.getSize();
			int rank = MPI.COMM_WORLD.getRank();
			int root = 0;
			int tag = 99;

			// Load the input image:
			ImagePlus inputImage = IJ.openImage(input);
			int width = inputImage.getWidth();

			// Split the workload:
			Workload workload = new Workload(inputImage, size);

			// Split the ImagePlus to new smaller ImagePlus part:
			ImageProcessor ip = inputImage.getProcessor();
			ip.setRoi(0, workload.getDisplacementHeightParts()[rank], width, (workload
				.getDisplacementHeightParts()[rank] + workload.getHeightParts()[rank]));
			ImagePlus resultImagePart = new ImagePlus("Result image part", ip.crop());

			// The node should process the image part:
			resultImagePart.getProcessor().blurGaussian(sigma);

			// Serialize and send the ImagePlus part to root:
			if (rank != 0) {
				// serialize the ImagePlus object:
				byte[] sendBuffer = (new FileSaver(resultImagePart)).serialize();
				MPI.COMM_WORLD.send(sendBuffer, sendBuffer.length, MPI.BYTE, root, tag);
			}

			if (rank == 0) {
				ImagePlus[] imageParts = new ImagePlus[size];
				imageParts[0] = resultImagePart;
				// Receive all image parts from all other nodes:
				for (int node = 1; node < size; node++) {
					int amount = width * workload.getHeightParts()[node];
					byte[] receiveBuffer = new byte[amount];
					MPI.COMM_WORLD.recv(receiveBuffer, amount, MPI.BYTE, node, tag);
					imageParts[node] = (new Opener()).deserialize(receiveBuffer);
				}

				// Create a single large result image:
				ImagePlus resultImagePlus = new ImagePlus("Result image", ip);
				// Merge images in the new image.
				for (int node = 0; node < size; node++) {
					for (int x = 0; x < width; x++) {
						for (int y = workload
							.getDisplacementHeightParts()[node]; y < (workload
								.getDisplacementHeightParts()[node] + workload
									.getHeightParts()[node]); y++)
						{
							ip.putPixel(x, y, imageParts[node].getPixel(x, y - workload
								.getDisplacementHeightParts()[node]));
						}
					}
				}

				// Save the result image:
				IJ.save(resultImagePlus, result);
			}
		}
		catch (MPIException exc) {
			logger.error(exc.getMessage());
		}
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Applies Gaussian blur on a given image.";
	}

	@Override
	public String parameters() {
		return "input image path, result image path, sigma (radius)";
	}
}
