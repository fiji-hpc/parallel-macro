
package cz.it4i.fiji.parallel_macro;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JniMpiParallelism implements Parallelism {

	private static MpiReflection mpiReflection;

	// Find and load the mpi.jar from OpenMPI:
	static {
		mpiReflection = new MpiReflection();
		String path = mpiReflection.findMpiJarFile();
		mpiReflection.loadOpenMpi(path);
	}

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
			log.error("MPI initialization error: {} ", exc.getMessage());
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
			log.error("MPI finalization error: {} ", exc.getMessage());
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
			log.error("MPI get rank error: {} ", exc.getMessage());
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
			log.error("MPI get size error: {} ", exc.getMessage());
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
			log.error("MPI barrier error: {} ", exc.getMessage());
		}
		return -1;
	}
}
