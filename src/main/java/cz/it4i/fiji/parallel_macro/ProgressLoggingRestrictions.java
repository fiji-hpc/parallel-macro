
package cz.it4i.fiji.parallel_macro;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressLoggingRestrictions {

	private Logger logger = LoggerFactory.getLogger(ParallelMacro.class
		.getName());

	protected boolean followsAddTaskRestrictions(boolean tasksWereReported) {
		// No new tasks should be added after they were reported:
		if (tasksWereReported) {
			logger.info(
				"addTask call was ignored - No new tasks should be added after they were reported.");
			return false;
		}
		return true;
	}

	public boolean followsReportTasksRestrictions(Map<Integer, String> tasks,
		boolean tasksWereReported)
	{
		if (tasks.isEmpty()) {
			logger.info(
				"reportTasks call was ignored, there are no tasks to report.");
			return false;
		}

		// The tasks should not be reported twice:
		return !tasksWereReported;
	}

	public boolean followsReportProgressRestrictions(Map<Integer, String> tasks,
		int taskId, int progress, Map<Integer, Integer> lastWrittenTaskPercentage)
	{
		// Check that task exists:
		if (!tasks.containsKey(taskId)) {
			logger.info(
				"Task {} does not exist. Progress can not be reported for a task that does not exist.",
				taskId);
			return false;
		}

		// Do not write progress percentage that has already been written to avoid
		// writing gigantic progress log files:
		return !(lastWrittenTaskPercentage.containsKey(taskId) &&
			progress <= lastWrittenTaskPercentage.get(taskId));
	}
}
