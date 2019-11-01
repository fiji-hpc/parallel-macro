
package cz.it4i.fiji.ij1_mpi_wrapper;

public interface Parallelism {

	public int initialise();

	public int finalise();

	public int getRank();

	public int getSize();

	public int barrier();

	public String scatterEqually(String sendString, int totalSendBufferLength,
		int root);

	public String scatter(String sendString, int sendCount, int receiveCount,
		int root);
}
