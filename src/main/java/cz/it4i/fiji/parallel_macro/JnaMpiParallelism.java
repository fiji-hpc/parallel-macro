
package cz.it4i.fiji.parallel_macro;

public class JnaMpiParallelism implements Parallelism {

	@Override
	public int getRank() {
		return MiniMpiUtils.getRank();
	}

	@Override
	public int getSize() {
		return MiniMpiUtils.getSize();
	}

	@Override
	public int barrier() {
		MiniMpiUtils.barrier();
		return 0;
	}
}
