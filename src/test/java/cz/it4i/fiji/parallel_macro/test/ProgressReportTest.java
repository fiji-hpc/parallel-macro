package cz.it4i.fiji.parallel_macro.test;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cz.it4i.fiji.parallel_macro.ProgressLogging;
import cz.it4i.fiji.parallel_macro.XmlProgressLogging;

public class ProgressReportTest {

	private ProgressLogging progressLogging;
	
	@Before
	public void initializeProgressLogging() {
		progressLogging = new XmlProgressLogging();
	}
	
	@Test
	public void progressShouldBeReportedOnlyIfTaskExists() {
		int rank = 0;
		
		progressLogging.reportProgress(0, 100, rank);
	}

	@Test
	public void tasksShouldBeReportedOnlyIfAtLeastOneExists() {
		int rank = 0;
		int size = 8;
		
		progressLogging.reportTasks(rank, size);
	}

	@Test
	public void tasksShouldBeReportedOnlyOnce() {
		int rank = 0;
		int size = 8;
		
		progressLogging.addTask("A task");
		progressLogging.reportTasks(rank, size);
		progressLogging.reportTasks(rank, size);
	}

	@Test
	public void addingTasksAfterTheyHaveBeenReportedShouldNotBePossible() {
		int rank = 0;
		int size = 8;
		
		progressLogging.addTask("A task");
		progressLogging.reportTasks(rank, size);
		progressLogging.reportTasks(rank, size);
		progressLogging.addTask("A second task");
	}

	@Test
	public void correctIdShouldBeAssignedToTaskWhenAdded() {
		progressLogging.addTask("Task one.");
		progressLogging.addTask("Task two.");
		int id = progressLogging.addTask("Task three.");
		assertEquals(2, id);
	}
}
