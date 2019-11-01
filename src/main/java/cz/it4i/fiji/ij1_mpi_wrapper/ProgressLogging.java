
package cz.it4i.fiji.ij1_mpi_wrapper;

public interface ProgressLogging {

	public int addTask(String description);

	public void reportTasks(int rank, int size);

	public int reportProgress(int taskId, int progress, int rank);

	public int reportText(String textToReport, int rank);
}
