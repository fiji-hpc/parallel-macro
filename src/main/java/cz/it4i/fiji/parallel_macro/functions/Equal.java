
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Equal implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Equal.class);

	@Override
	public void runFromMacro(Object[] parameters) {
		FunctionTemplates template = new FunctionTemplates();
		// Call the actual function:
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				template.runWith2DKernelImagesParallel(Kernels.equalParallel,
					EarlyEscapeConditions.equal, parameters);
			}
			else {
				template.runWith2DKernelImagesSerial(Kernels.equalSerial,
					EarlyEscapeConditions.equal, parameters);
			}
		}
		catch (MPIException e) {
			logger.error("An exception occurred!", e);
		}
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_STRING };
	}

	@Override
	public String description() {
		return "Flips an image in the dimension or dimensions specified.";
	}

	@Override
	public String parameters() {
		return "input image path, second input image path, result image path";
	}

}
