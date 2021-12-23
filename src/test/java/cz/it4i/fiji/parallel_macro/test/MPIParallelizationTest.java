
package cz.it4i.fiji.parallel_macro.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.it4i.fiji.parallel_macro.JnaMpiParallelism;
import cz.it4i.fiji.parallel_macro.Parallelism;

public class MPIParallelizationTest {

	private static Parallelism parallelism = new JnaMpiParallelism();

	@Test
	public void getRankAndSizeTest() {
		int rank = parallelism.getRank();
		int size = parallelism.getSize();
		assertTrue(rank >= 0 && size >= 1);
	}

}
