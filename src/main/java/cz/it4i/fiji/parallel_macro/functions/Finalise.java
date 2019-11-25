
package cz.it4i.fiji.parallel_macro.functions;

import cz.it4i.fiji.parallel_macro.MPIParallelism;
import cz.it4i.fiji.parallel_macro.Parallelism;

public class Finalise implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		Parallelism parallelism = MPIParallelism.getMPIParallelism();
		parallelism.finalise();
	}

	@Override
	public int[] parameterTypes() {
		return new int[] {};
	}

	@Override
	public String description() {
		return "Finalize parallelism.";
	}

	@Override
	public String parameters() {
		return "none";
	}

}
