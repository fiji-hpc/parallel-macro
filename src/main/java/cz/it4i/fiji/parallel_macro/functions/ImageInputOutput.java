package cz.it4i.fiji.parallel_macro.functions;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import mpi.MPI;

public class ImageInputOutput {

	private int width = 0;
	private int height = 0;
	private int imageType;

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return height;
	}

	public IntBuffer readImage(String imagePathString) {
		BufferedImage bufferedImage;
		IntBuffer imagePixels = null;
		try {
			bufferedImage = ImageIO.read(new File(imagePathString));
			this.width = bufferedImage.getWidth();
			this.height = bufferedImage.getHeight();
			this.imageType = bufferedImage.getType();

			// Load the image in an 1D array:
			imagePixels = MPI.newIntBuffer(this.width * this.height);

			for (int x = 0; x < this.width; x++) {
				for (int y = 0; y < this.height; y++) {
					imagePixels.put(x + y * this.width, bufferedImage.getRGB(x, y));
				}
			}
		}
		catch (IOException exc) {
			System.out.println("Error reading image.");
			exc.printStackTrace();
		}
		return imagePixels;
	}

	public void writeImage(String result, int[] imagePixels) {
		// Create an image buffer of the same size and type as the original image:
		BufferedImage imageReader = new BufferedImage(this.width, this.height,
			this.imageType);

		// Write all the pixel values to the buffer one by one:
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				imageReader.setRGB(x, y, imagePixels[x + y * this.width]);
			}
		}

		// Write the image to disk:
		try {
			ImageIO.write(imageReader, "jpg", new File(result));
		}
		catch (IOException exc) {
			System.out.println("Error writting image.");
			exc.printStackTrace();
		}
	}
}
