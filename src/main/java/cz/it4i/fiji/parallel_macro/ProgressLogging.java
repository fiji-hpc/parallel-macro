
package cz.it4i.fiji.parallel_macro;

public interface ProgressLogging {
	
	static final String LOG_FILE_PROGRESS_PREFIX = "progress_";
	static final String LOG_FILE_PROGRESS_POSTFIX = ".plog";

	public int addTask(String description);

	public void reportTasks(int rank, int size);

	public int reportProgress(int taskId, int progress, int rank);

	public void enableTiming();
}
