
package cz.it4i.fiji.ij1_mpi_wrapper;

public class ParallelMacro {

	private static Parallelism parallelism = new MPIParallelism();

	private static ProgressLogging progressLogging = null;
	
	private static TextReportLogging textReportLogging = new TextReportLogging();

	// This method resets the static state of the class:
	public static void resetState() {
		parallelism = new MPIParallelism();
	}
	
	public static void selectProgressLogger(String type) {
		if(progressLogging == null) {
			if(type.equals("xml")) {
				progressLogging = new XmlProgressLogging();
				return;
			}
			// By default use the file progress logging:
			progressLogging = new FileProgressLogging();
		}
	}

	public static int addTask(String description) {
		selectProgressLogger("");
		return progressLogging.addTask(description);
	}

	public static void reportTasks() {
		selectProgressLogger("");
		progressLogging.reportTasks(parallelism.getRank(), parallelism.getSize());
	}

	public static int reportProgress(int taskId, int progress) {
		selectProgressLogger("");
		return progressLogging.reportProgress(taskId, progress, parallelism
			.getRank());
	}

	public static int reportText(String textToReport) {
		return textReportLogging.reportText(textToReport, parallelism.getRank());
	}

	public static int initialise() {
		return parallelism.initialise();
	}

	public static int finalise() {
		return parallelism.finalise();
	}

	public static int getRank() {
		return parallelism.getRank();
	}

	public static int getSize() {
		return parallelism.getSize();
	}

	public static int barrier() {
		return parallelism.barrier();
	}

	// Simple scatter which attempts to split the send buffer to equal parts among
	// the nodes:
	public static String scatterEqually(String sendString,
		int totalSendBufferLength, int root)
	{
		return parallelism.scatterEqually(sendString, totalSendBufferLength, root);
	}

	public static String scatter(String sendString, int sendCount,
		int receiveCount, int root)
	{
		return parallelism.scatter(sendString, sendCount, receiveCount, root);
	}

	private ParallelMacro() {
		// Empty private constructor to hide default public one.
	}
}
