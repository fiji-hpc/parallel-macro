
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

public class Kernels {

	public static final Kernel set = (ImageInputOutput imageInputOutput,
		IntBuffer newImagePart, int width, int x, int y, List<Object> parameters,
		IntBuffer pixelsBuffer, int rank, Map<String, int[]> workload) -> {
		int value = (int) parameters.get(0);
		imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
			"displacementHeightParts")[rank]), new Color(value, value, value));
	};

	public static final Kernel addImageAndScalar = (
		ImageInputOutput imageInputOutput, IntBuffer newImagePart, int width, int x,
		int y, List<Object> parameters, IntBuffer pixelsBuffer, int rank,
		Map<String, int[]> workload) -> {
		int value = (int) parameters.get(0);
		int newValue = (new Color(value, value, value)).getRGB();
		imageInputOutput.setValueAt(newImagePart, width, x, (y - workload.get(
			"displacementHeightParts")[rank]), newValue + imageInputOutput.getValueAt(
				pixelsBuffer, width, x, y));
	};
}
