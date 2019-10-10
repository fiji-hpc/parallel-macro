package cz.it4i.fiji.ij1_mpi_wrapper.test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cz.it4i.fiji.ij1_mpi_wrapper.ProgressFileLogging;
import cz.it4i.fiji.ij1_mpi_wrapper.ProgressLogging;

public class ProgressReportTest {

	@Test
	public void progressShouldBeReportedOnlyIfTaskExists() {
		ProgressLogging progressLogging = new ProgressFileLogging();
		int rank = 0;
		
		progressLogging.reportProgress(0, 100, rank);
	}

	@Test
	public void tasksShouldBeReportedOnlyIfAtLeastOneExists() {
		ProgressLogging progressLogging = new ProgressFileLogging();
		int rank = 0;
		int size = 8;
		
		progressLogging.reportTasks(rank, size);
	}

	@Test
	public void tasksShouldBeReportedOnlyOnce() {
		ProgressLogging progressLogging = new ProgressFileLogging();
		int rank = 0;
		int size = 8;
		
		progressLogging.addTask("A task");
		progressLogging.reportTasks(rank, size);
		progressLogging.reportTasks(rank, size);
	}

	@Test
	public void addingTasksAfterTheyHaveBeenReportedShouldNotBePossible() {
		ProgressFileLogging progressLogging = new ProgressFileLogging();
		int rank = 0;
		int size = 8;
		
		progressLogging.addTask("A task");
		progressLogging.reportTasks(rank, size);
		progressLogging.reportTasks(rank, size);
		progressLogging.addTask("A second task");
	}

	@Test
	public void correctIdShouldBeAssignedToTaskWhenAdded() {
		ProgressFileLogging progressLogging = new ProgressFileLogging();
		progressLogging.addTask("Task one.");
		progressLogging.addTask("Task two.");
		int id = progressLogging.addTask("Task three.");
		assertEquals(2, id);
	}
}
