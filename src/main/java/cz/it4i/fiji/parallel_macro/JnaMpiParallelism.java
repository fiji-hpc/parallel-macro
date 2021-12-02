
package cz.it4i.fiji.parallel_macro;

public class JnaMpiParallelism implements Parallelism {

	static {
		System.out.println("JNA!");
	}

	@Override
	public int initialise() {
		MiniMpiUtils.Init();
		return 0;
	}

	@Override
	public int finalise() {
		MiniMpiUtils.Finalize();
		return 0;
	}

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
