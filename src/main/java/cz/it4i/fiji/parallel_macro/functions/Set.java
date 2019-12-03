
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Set implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Set.class);
	
	@Override
	public void runFromMacro(Object[] parameters) {
		FunctionTemplates template = new FunctionTemplates();
		// Call the actual function:
		try {
			if (MPI.COMM_WORLD.getSize() > 1) {
				template.runWith2DKernelParallel(Kernels.setParallel,
					EarlyEscapeConditions.set, parameters);
			}
			else {
				template.runWith2DKernelSerial(Kernels.setSerial,
					EarlyEscapeConditions.set, parameters);
			}
		}
		catch (MPIException exc) {
			logger.error("An exception occurred!", exc);
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
