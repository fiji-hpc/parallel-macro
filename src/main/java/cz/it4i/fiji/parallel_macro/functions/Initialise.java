
package cz.it4i.fiji.parallel_macro.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.it4i.fiji.parallel_macro.MPIParallelism;
import cz.it4i.fiji.parallel_macro.Parallelism;
import mpi.MPI;
import mpi.MPIException;

public class Initialise implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Initialise.class);

	@Override
	public void runFromMacro(Object[] parameters) {
		Parallelism parallelism = MPIParallelism.getMPIParallelism();
		parallelism.initialise();

		try {
			logger.info("MPI initialized with size: {}", MPI.COMM_WORLD.getSize());
		}
		catch (MPIException exc) {
			logger.error(exc.getMessage());
		}
	}

	@Override
	public int[] parameterTypes() {
		return new int[] {};
	}

	@Override
	public String description() {
		return "Initialize parallelism.";
	}

	@Override
	public String parameters() {
		return "none";
	}

}
