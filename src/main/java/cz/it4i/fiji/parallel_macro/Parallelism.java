
package cz.it4i.fiji.parallel_macro;

public interface Parallelism {

	public int getRank();

	public int getSize();

	public void barrier();
}
