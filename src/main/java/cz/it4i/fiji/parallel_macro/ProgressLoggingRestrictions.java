
package cz.it4i.fiji.parallel_macro;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressLoggingRestrictions {

	protected boolean followsAddTaskRestrictions(boolean tasksWereReported) {
		// No new tasks should be added after they were reported:
		if (tasksWereReported) {
			log.info(
				"addTask call was ignored - No new tasks should be added after they were reported.");
			return false;
		}
		return true;
	}

	public boolean followsReportTasksRestrictions(Map<Integer, String> tasks,
		boolean tasksWereReported)
	{
		if (tasks.isEmpty()) {
			log.info("reportTasks call was ignored, there are no tasks to report.");
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
			log.info(
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
