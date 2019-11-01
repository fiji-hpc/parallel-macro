package cz.it4i.fiji.ij1_mpi_wrapper.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.it4i.fiji.ij1_mpi_wrapper.MPIParallelism;
import cz.it4i.fiji.ij1_mpi_wrapper.Parallelism;

public class MPIParallelizationTest {
	private static Parallelism parallelism = new MPIParallelism();
	
	@BeforeClass
	public static void initializeMPI() {
		parallelism.initialise();
	}
	
	@AfterClass
	public static void finalizeMPI() {
		parallelism.finalise();
	}
	
	@Test
	public void getRankAndSizeTest() {
		int rank = parallelism.getRank();
		int size = parallelism.getSize();
		assertTrue(rank >= 0 && size >= 1);
	}

	@Test
	public void scatterShouldWorkWithNumbersStringAndBooleanTest() {
		int rank = parallelism.getRank();
		
		// Numbers:
		String sendString = "0, 1, 2, 3";
		String receivedString = parallelism.scatter(sendString, 1, 1, 0);
		assertTrue((rank+".0").equals(receivedString));
		
		// String:

		// Boolean:
	}

}
