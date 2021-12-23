// Based on a subset of scijava-parallel-mpi's JavaUtils.

package cz.it4i.fiji.parallel_macro;

import com.sun.jna.*;
import com.sun.jna.Native;

import java.lang.reflect.Field;

public class MiniMpiUtils {

	public static Pointer MPI_COMM_WORLD;
	public static Pointer currentComm;

	private static NativeLibrary mpilib;

	static {
		mpilib = NativeLibrary.getInstance("mpi");
		Init();
		Runtime.getRuntime().addShutdownHook(new Thread(MiniMpiUtils::Finalize));
	}

	private static void checkMpiResult(int ret) {
		if (ret != 0) {
			throw new RuntimeException("MPI failed with: " + ret);
		}
	}

	private static void Init() {
		int[] isInitialized = new int[1];
		checkMpiResult(MPILibrary.INSTANCE.MPI_Initialized(isInitialized));
		if (isInitialized[0] == 0) {
			checkMpiResult(MPILibrary.INSTANCE.MPI_Init(null, null));
		}
		for (Field f : MiniMpiUtils.class.getDeclaredFields()) {
			if (!f.getName().startsWith("MPI_")) {
				continue;
			}

			try {
				f.set(null, getSymbolPtr("ompi_" + f.getName().toLowerCase()));
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		currentComm = MPI_COMM_WORLD;
	}

	private static void Finalize() {
		int[] isFinalized = new int[1];
		checkMpiResult(MPILibrary.INSTANCE.MPI_Finalized(isFinalized));
		if (isFinalized[0] == 0) {
			MPILibrary.INSTANCE.MPI_Finalize();
		}
	}

	public static int getSize() {
		int[] rank = new int[1];
		checkMpiResult(MPILibrary.INSTANCE.MPI_Comm_size(currentComm, rank));
		return rank[0];
	}

	public static int getRank() {
		int[] rank = new int[1];
		checkMpiResult(MPILibrary.INSTANCE.MPI_Comm_rank(currentComm, rank));
		return rank[0];
	}

	public static void barrier() {
		checkMpiResult(MPILibrary.INSTANCE.MPI_Barrier(currentComm));
	}

	private static Pointer getSymbolPtr(String name) {
		return mpilib.getGlobalVariableAddress(name);
	}

	public interface MPILibrary extends Library {

		MPILibrary INSTANCE = Native.load("mpi", MPILibrary.class);

		int MPI_Initialized(int[] flag);

		int MPI_Finalized(int[] flag);

		int MPI_Init(Pointer argv, Pointer argc);

		int MPI_Finalize();

		int MPI_Comm_rank(Pointer comm, int[] rank);

		int MPI_Comm_size(Pointer comm, int[] size);

		int MPI_Barrier(Pointer comm);
	}

}
