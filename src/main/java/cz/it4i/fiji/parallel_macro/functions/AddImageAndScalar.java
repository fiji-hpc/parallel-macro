
package cz.it4i.fiji.parallel_macro.functions;

import java.util.ArrayList;
import java.util.List;
import ij.macro.MacroExtension;

public class AddImageAndScalar implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		String input = (String) parameters[0];
		String result = (String) parameters[1];
		int value = (int) ((double) parameters[2]);

		// Call the actual function:
		parallelAddImageAndScalar(input, result, value);
	}

	private void parallelAddImageAndScalar(String input, String result, int value) {
		// Set the parameters, namely the value to set all pixels to:
		FunctionTemplate2D template = new FunctionTemplate2D();
		List<Object> parameters = new ArrayList<>();
		parameters.add(value);

		template.runWithKernel(input, result, Kernels.addImageAndScalar, parameters);
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Add to all pixels to the given value.";
	}

	@Override
	public String parameters() {
		return "input image path, result image path, value";
	}
}
