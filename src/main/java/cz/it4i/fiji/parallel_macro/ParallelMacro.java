
package cz.it4i.fiji.parallel_macro;

public class ParallelMacro {

	private static Parallelism parallelism = new JnaMpiParallelism();

	private static ProgressLogging progressLogging = null;

	private static TextReportLogging textReportLogging = new TextReportLogging();

	public static void selectProgressLogger(String type) {
		if (progressLogging == null) {
			if (type.equalsIgnoreCase("csv")) {
				progressLogging = new CsvProgressLogging();
				return;
			}
			// By default use the XML progress logging:
			progressLogging = new XmlProgressLogging();
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

	public static void enableTiming() {
		selectProgressLogger("");
		progressLogging.enableTiming();
	}

	public static int reportText(String textToReport) {
		return textReportLogging.reportText(textToReport, parallelism.getRank());
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

	private ParallelMacro() {
		// Empty private constructor to hide default public one.
	}
}
