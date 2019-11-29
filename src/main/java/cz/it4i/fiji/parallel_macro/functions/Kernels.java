
package cz.it4i.fiji.parallel_macro.functions;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.Map;

public class Kernels {

	public static final Kernel set = (ImageInputOutput imageInputOutput, IntBuffer newImagePart,
		int width, int x, int y, int value, int rank,
		Map<String, int[]> workload) -> imageInputOutput.setValueAt(newImagePart,
			width, x, (y - workload.get("displacementHeightParts")[rank]), new Color(
				value, value, value));
}
