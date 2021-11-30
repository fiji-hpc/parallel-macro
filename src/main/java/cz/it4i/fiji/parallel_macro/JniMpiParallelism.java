
package cz.it4i.fiji.parallel_macro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JniMpiParallelism implements Parallelism {

	private static MpiReflection mpiReflection;

	// Find and load the mpi.jar from OpenMPI:
	static {
		mpiReflection = new MpiReflection();
		String path = mpiReflection.findMpiJarFile();
		mpiReflection.loadOpenMpi(path);
	}

	Logger logger = LoggerFactory.getLogger(JniMpiParallelism.class);

	@Override
	public int initialise() {
		String[] arg0 = { "one", "two" };
		try {
			if (!mpiReflection.isInitialised()) {
				mpiReflection.initialise(arg0);
			}
			return 0;
		}
		catch (Exception exc) {
			logger.error("MPI initialization error: {} ", exc.getMessage());
			return -1;
		}
	}

	@Override
	public int finalise() {
		try {
			if (!mpiReflection.isFinalised()) {
				mpiReflection.finalise();
			}
			return 0;
		}
		catch (Exception exc) {
			logger.error("MPI finalization error: {} ", exc.getMessage());
		}
		return -1;
	}

	@Override
	public int getRank() {
		int rank = -1;
		try {
			rank = mpiReflection.getRank();
		}
		catch (Exception exc) {
			logger.error("MPI get rank error: {} ", exc.getMessage());
		}
		return rank;
	}

	@Override
	public int getSize() {
		int size = -1;
		try {
			size = mpiReflection.getSize();
		}
		catch (Exception exc) {
			logger.error("MPI get size error: {} ", exc.getMessage());
		}
		return size;
	}

	@Override
	public int barrier() {
		try {
			mpiReflection.barrier();
			return 0;
		}
		catch (Exception exc) {
			logger.error("MPI barrier error: {} ", exc.getMessage());
		}
		return -1;
	}
}
