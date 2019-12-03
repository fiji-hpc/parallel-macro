
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class AddImageAndScalar implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(
		AddImageAndScalar.class);

	@Override
	public void runFromMacro(Object[] parameters) {
		FunctionTemplates template = new FunctionTemplates();
		try {
			// Call the actual function:
			if (MPI.COMM_WORLD.getRank() > 1) {
				template.runWith2DKernelParallel(Kernels.addImageAndScalarParallel,
					EarlyEscapeConditions.addImageAndScalar, parameters);
			}
			else {
				template.runWith2DKernelSerial(Kernels.addImageAndScalarSerial,
					EarlyEscapeConditions.addImageAndScalar, parameters);
			}
		}
		catch (MPIException e) {
			logger.error("An exception occurred!", e);
		}
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
