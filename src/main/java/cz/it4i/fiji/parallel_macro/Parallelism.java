
package cz.it4i.fiji.parallel_macro;

public interface Parallelism {

	public int initialise();

	public int finalise();

	public int getRank();

	public int getSize();

	public int barrier();
}
