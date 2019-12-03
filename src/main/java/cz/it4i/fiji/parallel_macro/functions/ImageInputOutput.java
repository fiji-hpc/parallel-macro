
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mpi.MPI;

public class ImageInputOutput {

	private final Logger logger = LoggerFactory.getLogger(ImageInputOutput.class);

	public IntBufferImage readImage(String imagePathString) {
		BufferedImage bufferedImage;
		IntBufferImage newImage = null;
		try {
			bufferedImage = ImageIO.read(new File(imagePathString));
			int imageWidth = bufferedImage.getWidth();
			int imageHeight = bufferedImage.getHeight();
			int imageType = bufferedImage.getType();

			// Load the image in an 1D array:
			IntBuffer imagePixels = MPI.newIntBuffer(imageWidth * imageHeight);

			newImage = new IntBufferImage(imageWidth, imageHeight, imageType,
				imagePixels);

			bufferedImageToIntBuffer(bufferedImage, imagePixels);
		}
		catch (IOException exc) {
			logger.error("Error reading image. {}", exc.getMessage());
		}
		return newImage;
	}

	public void writeImage(String result, IntBufferImage image) {
		// Create an image buffer of the same size and type as the original image:
		BufferedImage imageReader = new BufferedImage(image.getWidth(), image
			.getHeight(), image.getType());

		// Write all the pixel values to the buffer:
		intBufferToBufferedImage(image.getIntBuffer(), imageReader);

		// Write the image to disk:
		try {
			ImageIO.write(imageReader, "jpg", new File(result));
		}
		catch (IOException exc) {
			logger.error("Error writting image. {}", exc.getMessage());
		}
	}

	public void setValueAt(IntBuffer pixels, int width, int x, int y,
		Color color)
	{
		setValueAt(pixels, width, x, y, color.getRGB());
	}

	public void setValueAt(IntBuffer pixels, int width, int x, int y, int value) {
		pixels.put(x + y * width, value);
	}

	public int getValueAt(IntBufferImage image, int width, int x, int y) {
		return image.getIntBuffer().get(x + y * width);
	}

	public void intBufferToBufferedImage(IntBuffer intBuffer,
		BufferedImage bufferedImage)
	{
		int[] tempArray = new int[intBuffer.capacity()];
		intBuffer.rewind();
		intBuffer.get(tempArray);
		bufferedImage.setRGB(0, 0, bufferedImage.getWidth(), bufferedImage
			.getHeight(), tempArray, 0, bufferedImage.getWidth());
	}

	public void bufferedImageToIntBuffer(BufferedImage bufferedImage,
		IntBuffer intBuffer)
	{
		int[] tempArray = new int[intBuffer.capacity()];
		bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage
			.getHeight(), tempArray, 0, bufferedImage.getWidth());
		intBuffer.rewind();
		intBuffer.put(tempArray);
	}
}
