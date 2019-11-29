
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import mpi.MPI;

public class ImageInputOutput {

	private int imageWidth = 0;
	private int imageHeight = 0;
	private int imageType;

	public int getWidth() {
		return this.imageWidth;
	}

	public int getHeight() {
		return imageHeight;
	}

	public IntBuffer readImage(String imagePathString) {
		BufferedImage bufferedImage;
		IntBuffer imagePixels = null;
		try {
			bufferedImage = ImageIO.read(new File(imagePathString));
			this.imageWidth = bufferedImage.getWidth();
			this.imageHeight = bufferedImage.getHeight();
			this.imageType = bufferedImage.getType();

			// Load the image in an 1D array:
			imagePixels = MPI.newIntBuffer(this.imageWidth * this.imageHeight);

			for (int x = 0; x < this.imageWidth; x++) {
				for (int y = 0; y < this.imageHeight; y++) {
					imagePixels.put(x + y * this.imageWidth, bufferedImage.getRGB(x, y));
				}
			}
		}
		catch (IOException exc) {
			System.out.println("Error reading image.");
			exc.printStackTrace();
		}
		return imagePixels;
	}

	public void writeImage(String result, IntBuffer buffer) {
		// Create an image buffer of the same size and type as the original image:
		BufferedImage imageReader = new BufferedImage(this.imageWidth,
			this.imageHeight, this.imageType);

		// Write all the pixel values to the buffer one by one:
		for (int x = 0; x < this.imageWidth; x++) {
			for (int y = 0; y < this.imageHeight; y++) {
				imageReader.setRGB(x, y, buffer.get(x + y * this.imageWidth));
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
	
	public void setValueAt(IntBuffer pixels, int width, int x, int y, Color color) {
		setValueAt(pixels, width, x, y, color.getRGB());
	}

	public void setValueAt(IntBuffer pixels, int width, int x, int y, int value) {
		pixels.put(x + y * width, value);
	}

	public int getValueAt(IntBuffer pixels, int width, int x, int y) {
		return pixels.get(x + y * width);
	}

	// Returns parts split by image height, displacement by image height, count of
	// elements in the 1D array part and displacements in elements in the 1D array
	// part.
	public Map<String, int[]> splitWorkLoad(int size) {
		int[] heightParts = new int[size];
		int[] displacementHeightParts = new int[size];
		int[] counts = new int[size];
		int[] displacements = new int[size];

		for (int loopRank = 0; loopRank < size; loopRank++) {
			heightParts[loopRank] = this.imageHeight / size;
			displacementHeightParts[loopRank] = loopRank * heightParts[loopRank];
			if (loopRank == size - 1) {
				// The last node should also do any remaining work.
				heightParts[loopRank] += this.imageHeight % size;
			}
			counts[loopRank] = heightParts[loopRank] * this.imageWidth;
			displacements[loopRank] = displacementHeightParts[loopRank] *
				this.imageWidth;
		}

		Map<String, int[]> splitWorkload = new HashMap<>();
		splitWorkload.put("heightParts", heightParts);
		splitWorkload.put("displacementHeightParts", displacementHeightParts);
		splitWorkload.put("counts", counts);
		splitWorkload.put("displacements", displacements);
		return splitWorkload;
	}
}
