
package cz.it4i.fiji.parallel_macro;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProgressLogging extends ProgressLoggingRestrictions implements
	ProgressLogging
{

	private Logger logger = LoggerFactory.getLogger(ParallelMacro.class);

	private Map<Integer, String> tasks = new HashMap<>();

	private Integer numberOfTasks = 0;

	private Map<Integer, Integer> lastWrittenTaskPercentage = new HashMap<>();

	private boolean tasksWereReported = false;

	// Timing is disabled by default for performance.
	private boolean timingIsEnabled = false;
	private Map<Integer, Long> startTime = new HashMap<>();

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

			// Write the number of nodes on the first line of the progress log
			Files.write(progressLogFilePath, Integer.toString(size).concat(System
				.lineSeparator()).getBytes(), StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE);

			// Write the first time-stamp:
			Files.write(progressLogFilePath, Long.toString(java.time.Instant.now()
				.toEpochMilli()).concat(System.lineSeparator()).getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);

			// Report the tasks one by ones id each along with its description:
			for (Integer counter = 0; counter < numberOfTasks; counter++) {
				text = String.valueOf(counter).concat(",").concat(tasks.get(counter))
					.concat(System.lineSeparator());
				Files.write(progressLogFilePath, text.getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}

		}
		catch (IOException exc) {
			logger.error(" Error occurred during reporting tasks: {} ", exc
				.getMessage());
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
				progress));

			// Note time that a task took to finish (first % noted to 100%).
			if (timingIsEnabled) {
				long endTime;
				if (progress == 0 || !startTime.containsKey(taskId)) {
					startTime.put(taskId, System.nanoTime());
				}
				else if (progress == 100) {
					endTime = System.nanoTime() - startTime.get(taskId);
					text = text.concat(",").concat(String.valueOf(endTime));
				}
			}

			text = text.concat(System.lineSeparator());

			Files.write(progressLogFilePath, text.getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			updateLastUpdatedTimestamp(rank); // Note the time-stamp of the update
		}
		catch (IOException exc) {
			logger.error(" Error occurred during report progress error: {} ", exc
				.getMessage());
			return -1;
		}
		return 0;
	}

	private void updateLastUpdatedTimestamp(int rank) {
		try (RandomAccessFile writer = new RandomAccessFile(
			LOG_FILE_PROGRESS_PREFIX + String.valueOf(rank) +
				LOG_FILE_PROGRESS_POSTFIX, "rw"))
		{
			writer.readLine(); // Ignore, the first line is the number of nodes.
			writer.writeBytes(Long.toString(Instant.now().toEpochMilli()).concat(
				System.lineSeparator()));
		}
		catch (IOException exc) {
			logger.error("Error occurred while updating last updated timestamp: {} ",
				exc.getMessage());
		}

	}

	@Override
	public void enableTiming() {
		this.timingIsEnabled = true;
	}

}
