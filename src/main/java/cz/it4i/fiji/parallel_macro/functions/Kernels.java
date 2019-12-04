
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;

public class Kernels {

	private static final ImageInputOutput IMAGE_INPUT_OUTPUT =
		new ImageInputOutput();

	interface ParallelKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(IntBuffer newImagePart, int x, int y, Object[] parameters,
			IntBufferImage oldImage, int rank, Workload workload);
	}

	interface SerialKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(int x, int y, Object[] parameters, IntBufferImage oldImage,
			IntBuffer newImageBuffer);
	}

	interface SerialImagesKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(int x, int y, Object[] parameters, IntBufferImage oldImage1,
			IntBufferImage oldImage2, IntBuffer newImageBuffer);
	}

	interface ParallelImagesKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(IntBuffer newImagePart, int x, int y, Object[] parameters,
			IntBufferImage oldImage1, IntBufferImage oldImage2, int rank,
			Workload workload);
	}

	private Kernels() {
		// Private constructor to hide the implicit public one.
	}

	public static final ParallelKernel setParallel = (IntBuffer newImagePart,
		int x, int y, Object[] parameters, IntBufferImage image, int rank,
		Workload workload) -> {
		int value = (int) ((double) parameters[2]);

		int width = image.getWidth();

		IMAGE_INPUT_OUTPUT.setValueAt(newImagePart, width, x, (y - workload
			.getDisplacementHeightParts()[rank]), new Color(value, value, value));
	};

	public static final ParallelKernel flipParallel = (IntBuffer newImagePart,
		int x, int y, Object[] parameters, IntBufferImage pixelsBuffer, int rank,
		Workload workload) -> {
		double flipXString = (double) parameters[2];
		double flipYString = (double) parameters[3];

		// Convert input to proper types, Macro always uses double for numbers and
		// booleans:
		boolean flipX = (flipXString == 1.0);
		boolean flipY = (flipYString == 1.0);

		int width = pixelsBuffer.getWidth();
		int height = pixelsBuffer.getHeight();

		IMAGE_INPUT_OUTPUT.setValueAt(newImagePart, width, x, (y - workload
			.getDisplacementHeightParts()[rank]), IMAGE_INPUT_OUTPUT.getValueAt(
				pixelsBuffer, width, (flipX) ? (width - 1 - x) : x, flipY ? (height -
					1) - y : y));
	};

	public static final ParallelKernel addImageAndScalarParallel = (
		IntBuffer newImagePart, int x, int y, Object[] parameters,
		IntBufferImage oldImage, int rank, Workload workload) -> {
		int value = (int) ((double) parameters[2]);

		int width = oldImage.getWidth();

		int red = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getRed() + value;
		if (red > 255) red = 255;
		else if (red < 0) red = 0;
		int green = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getGreen() + value;
		if (green > 255) green = 255;
		else if (green < 0) green = 0;
		int blue = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getBlue() + value;
		if (blue > 255) blue = 255;
		else if (blue < 0) blue = 0;

		IMAGE_INPUT_OUTPUT.setValueAt(newImagePart, width, x, (y - workload
			.getDisplacementHeightParts()[rank]), new Color(red, green, blue));
	};

	public static final SerialKernel addImageAndScalarSerial = (int x, int y,
		Object[] parameters, IntBufferImage oldImage, IntBuffer newImageBuffer) -> {
		int value = (int) ((double) parameters[2]);

		int width = oldImage.getWidth();

		int red = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getRed() + value;
		if (red > 255) red = 255;
		else if (red < 0) red = 0;
		int green = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getGreen() + value;
		if (green > 255) green = 255;
		else if (green < 0) green = 0;
		int blue = new Color(IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, x, y))
			.getBlue() + value;
		if (blue > 255) blue = 255;
		else if (blue < 0) blue = 0;

		IMAGE_INPUT_OUTPUT.setValueAt(newImageBuffer, width, x, y, new Color(red,
			green, blue));
	};

	public static final SerialKernel setSerial = (int x, int y,
		Object[] parameters, IntBufferImage oldImage, IntBuffer newImageBuffer) -> {
		int width = oldImage.getWidth();

		int value = (int) ((double) parameters[2]);
		IMAGE_INPUT_OUTPUT.setValueAt(newImageBuffer, width, x, y, new Color(value,
			value, value));
	};

	public static final SerialKernel flipSerial = (int x, int y,
		Object[] parameters, IntBufferImage oldImage, IntBuffer newImageBuffer) -> {
		int width = oldImage.getWidth();
		int height = oldImage.getHeight();
		double flipXString = (double) parameters[2];
		double flipYString = (double) parameters[3];

		// Convert input to proper types, Macro uses double for boolean:
		boolean flipX = (flipXString == 1.0);
		boolean flipY = (flipYString == 1.0);

		IMAGE_INPUT_OUTPUT.setValueAt(newImageBuffer, width, x, y,
			IMAGE_INPUT_OUTPUT.getValueAt(oldImage, width, (flipX) ? (width - 1 - x)
				: x, flipY ? (height - 1) - y : y));
	};

	public static final SerialImagesKernel equalSerial = (int x, int y,
		Object[] parameters, IntBufferImage oldImage1, IntBufferImage oldImage2,
		IntBuffer newImageBuffer) -> {
		int width = oldImage1.getWidth();
		int pixelImage1 = IMAGE_INPUT_OUTPUT.getValueAt(oldImage1, width, x, y);
		int pixelImage2 = IMAGE_INPUT_OUTPUT.getValueAt(oldImage2, width, x, y);
		int newValue = (pixelImage1 == pixelImage2) ? 255 : 0;
		IMAGE_INPUT_OUTPUT.setValueAt(newImageBuffer, width, x, y, new Color(
			newValue, newValue, newValue));
	};

	public static final ParallelImagesKernel equalParallel = (
		IntBuffer newImagePart, int x, int y, Object[] parameters,
		IntBufferImage oldImage1, IntBufferImage oldImage2, int rank,
		Workload workload) -> {
		int width = oldImage1.getWidth();
		int pixelImage1 = IMAGE_INPUT_OUTPUT.getValueAt(oldImage1, width, x, y);
		int pixelImage2 = IMAGE_INPUT_OUTPUT.getValueAt(oldImage2, width, x, y);
		int newValue = (pixelImage1 == pixelImage2) ? 255 : 0;
		IMAGE_INPUT_OUTPUT.setValueAt(newImagePart, width, x, (y - workload
			.getDisplacementHeightParts()[rank]), new Color(newValue, newValue,
				newValue));
	};

}
