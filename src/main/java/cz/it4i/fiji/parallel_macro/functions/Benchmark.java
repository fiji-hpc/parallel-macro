
package cz.it4i.fiji.parallel_macro.functions;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.macro.MacroExtension;
import mpi.MPI;
import mpi.MPIException;

public class Benchmark implements MyMacroExtensionDescriptor {

	private final Logger logger = LoggerFactory.getLogger(Benchmark.class);

	@Override
	public void runFromMacro(Object[] parameters) {
		runBenchmark(parameters);
	}

	private void runBenchmark(Object[] parameters) {
		try {
			int size = MPI.COMM_WORLD.getSize();
			if (size < 2) {
				logger.error("The benchmark requires more than one node to run.");
			}
			else {
				final int NUMBER_OF_TRIALS = 100;
				int rank = MPI.COMM_WORLD.getRank();

				double startTime;
				double endTime;
				double[] samples = new double[NUMBER_OF_TRIALS];

				// Benchmark flip:
				Flip flip = new Flip();
				Object[] flipParameters = new Object[4];
				flipParameters[0] = parameters[0]; // Input image path.
				flipParameters[1] = parameters[2]; // Result image path.
				flipParameters[2] = 1.0; // FlipX is true.
				flipParameters[3] = 1.0; // FlipY is true.

				for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
					startTime = MPI.wtime();
					flip.runFromMacro(flipParameters);
					endTime = MPI.wtime();
					samples[i] = endTime - startTime;
				}

				MPI.COMM_WORLD.barrier();

				if (rank == 0) {
					System.out.println("Parallel flip, nodes: " + size + " median time: " +
						calculateMedian(samples));
				}

			}
		}
		catch (MPIException exc) {
			logger.error("An exception occured.", exc);
		}
	}

	private double calculateMedian(double[] samples) {
		Arrays.sort(samples);
		if (samples.length % 2 == 0) {
			return (samples[samples.length / 2] + samples[samples.length / 2 - 1]) /
				2;
		}
		return samples[samples.length / 2];

	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING, MacroExtension.ARG_STRING,
			MacroExtension.ARG_STRING };
	}

	@Override
	public String description() {
		return "This is a benchmark of Parallel Macro, it requires more than one node to run.";
	}

	@Override
	public String parameters() {
		return "input image path, second input image path, result image path";
	}

}
