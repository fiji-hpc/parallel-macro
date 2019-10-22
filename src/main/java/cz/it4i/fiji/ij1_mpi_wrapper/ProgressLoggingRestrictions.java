
package cz.it4i.fiji.ij1_mpi_wrapper;

import java.util.Map;
import java.util.logging.Logger;

public class ProgressLoggingRestrictions {

	private Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	protected boolean followsAddTaskRestrictions(boolean tasksWereReported) {
		// No new tasks should be added after they were reported:
		if (tasksWereReported) {
			logger.warning(
				"addTask call was ignored - No new tasks should be added after they were reported.");
			return false;
		}
		return true;
	}

	public boolean followsReportTasksRestrictions(Map<Integer, String> tasks,
		boolean tasksWereReported)
	{
		if (tasks.isEmpty()) {
			logger.warning(
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
			logger.warning("Task " + taskId +
				" does not exist. Progress can not be reported for a task that does not exist.");
			return false;
		}

		// Do not write progress percentage that has already been written to avoid
		// writing gigantic progress log files:
		if (lastWrittenTaskPercentage.containsKey(taskId) &&
			progress <= lastWrittenTaskPercentage.get(taskId))
		{
			return false;
		}

		return true;
	}
}
