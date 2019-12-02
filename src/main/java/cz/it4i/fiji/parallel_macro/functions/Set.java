
package cz.it4i.fiji.parallel_macro.functions;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Set implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		FunctionTemplates template = new FunctionTemplates();
		// Call the actual function:
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				template.runWith2DKernelParallel(Kernels.setParallel, parameters);
			}
			else {
				template.runWith2DKernelSerial(Kernels.setSerial, parameters);
			}
		}
		catch (MPIException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Sets all pixels to the given value.";
	}

	@Override
	public String parameters() {
		return "input image path, result image path, value";
	}

}
