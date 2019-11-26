
package cz.it4i.fiji.parallel_macro.functions;

import cz.it4i.fiji.parallel_macro.MPIParallelism;
import cz.it4i.fiji.parallel_macro.Parallelism;
import mpi.MPI;
import mpi.MPIException;

public class Initialise implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		Parallelism parallelism = MPIParallelism.getMPIParallelism();
		parallelism.initialise();
		
		try {
			System.out.println("MPI initialized with size: "+MPI.COMM_WORLD.getSize());
		} catch (MPIException e) {
			e.printStackTrace();
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
