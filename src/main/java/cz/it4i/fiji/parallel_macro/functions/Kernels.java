
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.Map;

public class Kernels {

	interface ParallelKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(ImageInputOutput imageInputOutput, IntBuffer newImagePart,
			int width, int height, int x, int y, Object[] parameters,
			IntBuffer imagePixels, int rank, Map<String, int[]> workload);
	}

	interface SerialKernel {

		// Abstract function for the parallel kernels of the math operations:
		void compute(ImageInputOutput imageInputOutput, int width, int height,
			int x, int y, Object[] parameters, IntBuffer oldImageBuffer,
			IntBuffer newImageBuffer);
	}

	private Kernels() {
		// Private constructor to hide the implicit public one.
	}

	public static final ParallelKernel setParallel = (
		ImageInputOutput imageInputOutput, IntBuffer newImagePart, int width,
		int height, int x, int y, Object[] parameters, IntBuffer pixelsBuffer,
		int rank, Map<String, int[]> workload) -> {
		int value = (int) ((double) parameters[2]);
		imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
			"displacementHeightParts")[rank]), new Color(value, value, value));
	};

	public static final ParallelKernel flipParallel = (
		ImageInputOutput imageInputOutput, IntBuffer newImagePart, int width,
		int height, int x, int y, Object[] parameters, IntBuffer pixelsBuffer,
		int rank, Map<String, int[]> workload) -> {
		double flipXString = (double) parameters[2];
		double flipYString = (double) parameters[3];

		// Convert input to proper types, Macro always uses double for numbers and
		// booleans:
		boolean flipX = (flipXString == 1.0);
		boolean flipY = (flipYString == 1.0);

		imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
			"displacementHeightParts")[rank]), imageInputOutput.getValueAt(
				pixelsBuffer, width, (flipX) ? (width - 1 - x) : x, flipY ? (height -
					1) - y : y));
	};

	public static final ParallelKernel addImageAndScalarParallel = (
		ImageInputOutput imageInputOutput, IntBuffer newImagePart, int width,
		int height, int x, int y, Object[] parameters, IntBuffer pixelsBuffer,
		int rank, Map<String, int[]> workload) -> {
		int value = (int) ((double) parameters[2]);
		int newValue = (new Color(value, value, value)).getRGB();
		imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
			"displacementHeightParts")[rank]), newValue + imageInputOutput.getValueAt(
				pixelsBuffer, width, x, y));
	};

	public static final SerialKernel setSerial = (
		ImageInputOutput imageInputOutput, int width, int height, int x, int y,
		Object[] parameters, IntBuffer oldImageBuffer,
		IntBuffer newImageBuffer) -> {
		int value = (int) ((double) parameters[2]);
		imageInputOutput.setValueAt(newImageBuffer, width, x, y, new Color(value,
			value, value));
	};

	public static final SerialKernel flipSerial = (
		ImageInputOutput imageInputOutput, int width, int height, int x, int y,
		Object[] parameters, IntBuffer oldImageBuffer,
		IntBuffer newImageBuffer) -> {
		double flipXString = (double) parameters[2];

		// Convert input to proper types, Macro uses double for boolean:
		boolean flipX = (flipXString == 1.0);

		imageInputOutput.setValueAt(newImageBuffer, width, x, y, imageInputOutput
			.getValueAt(oldImageBuffer, width, (flipX) ? ((width - 1) - x) : x,
				(flipX) ? y : ((height - 1) - y)));
	};

}
