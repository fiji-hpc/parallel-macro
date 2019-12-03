
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Flip implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Flip.class);

	@Override
	public void runFromMacro(Object[] parameters) {

		FunctionTemplates template = new FunctionTemplates();
		// Call the actual function:
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				template.runWith2DKernelParallel(Kernels.flipParallel,
					EarlyEscapeConditions.flip, parameters);
			}
			else {
				template.runWith2DKernelSerial(Kernels.flipSerial,
					EarlyEscapeConditions.flip, parameters);
			}
		}
		catch (MPIException e) {
			logger.error("An exception occurred!", e);
		}
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_NUMBER, MacroExtension.ARG_NUMBER };
	}

	@Override
	public String description() {
		return "Flips an image in the dimension or dimensions specified.";
	}

	@Override
	public String parameters() {
		return "input image path, result image path, flipX, flipY";
	}
}
