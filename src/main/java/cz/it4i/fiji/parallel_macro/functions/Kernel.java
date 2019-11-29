
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;
import java.util.Map;

public interface Kernel {

	// Abstract function for the kernels of the math operations:
	void compute(ImageInputOutput imageInputOutput, IntBuffer newImagePart,
		int width, int x, int y, Object aValue, int rank,
		Map<String, int[]> workload);
}
