
package cz.it4i.fiji.parallel_macro.functions;

import ij.macro.MacroExtension;

public class AddImageAndScalar implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		FunctionTemplates template = new FunctionTemplates();

		// Call the actual function:
		template.runWith2DKernelParallel(Kernels.addImageAndScalarParallel, parameters);
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
