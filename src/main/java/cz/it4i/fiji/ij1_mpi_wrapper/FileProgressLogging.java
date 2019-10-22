
package cz.it4i.fiji.ij1_mpi_wrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileProgressLogging extends ProgressLoggingRestrictions implements
	ProgressLogging
{

	private Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	private Map<Integer, String> tasks = new HashMap<>();

	private Integer numberOfTasks = 0;

	private Map<Integer, Integer> lastWrittenTaskPercentage = new HashMap<>();

	private boolean tasksWereReported = false;

	@Override
	public int addTask(String description) {
		if (!super.followsAddTaskRestrictions(tasksWereReported)) {
			return -1;
		}

		tasks.put(numberOfTasks, description);
		return numberOfTasks++;
	}

	@Override
	public void reportTasks(int rank, int size) {
		if (!super.followsReportTasksRestrictions(tasks, tasksWereReported)) {
			return;
		}

		try {
			String text = "";

			Path progressLogFilePath = Paths.get(LOG_FILE_PROGRESS_PREFIX + String
				.valueOf(rank) + LOG_FILE_PROGRESS_POSTFIX);

			Files.write(progressLogFilePath, Integer.toString(size).concat(System
				.lineSeparator()).getBytes(), StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE);

			for (Integer counter = 0; counter < numberOfTasks; counter++) {
				text = String.valueOf(counter).concat(",").concat(tasks.get(counter))
					.concat(System.lineSeparator());
				Files.write(progressLogFilePath, text.getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}

		}
		catch (IOException exc) {
			logger.warning("" + exc.getMessage());
		}

		// The tasks should not be reported twice:
		tasksWereReported = true;
	}

	@Override
	public int reportProgress(int taskId, int progress, int rank) {
		if (!super.followsReportProgressRestrictions(tasks, taskId, progress,
			lastWrittenTaskPercentage))
		{
			return -1;
		}

		// Ignore impossible new progress percentages:
		if (progress > 100 || progress < 0) {
			return lastWrittenTaskPercentage.get(taskId);
		}
		
		lastWrittenTaskPercentage.put(taskId, progress);

		try {
			Path progressLogFilePath = Paths.get(LOG_FILE_PROGRESS_PREFIX + String
				.valueOf(rank) + LOG_FILE_PROGRESS_POSTFIX);
			String text = String.valueOf(taskId).concat(",").concat(String.valueOf(
				progress)).concat(System.lineSeparator());
			Files.write(progressLogFilePath, text.getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException exc) {
			logger.warning("reportProgress error - " + exc.getMessage());
			return -1;
		}
		return 0;
	}

}
