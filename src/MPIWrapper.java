import mpi.MPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MPIWrapper {

	private static final Logger LOGGER = Logger.getLogger(MPIWrapper.class
		.getName());

	private static String logFileProgressPrefix = "progress_";
	private static String logFileReportPrefix = "report_";

	private static Map<Integer, String> tasks = new HashMap<>();

	private static Integer numberOfTasks = 0;

	public static void addTask(String description) {
		tasks.put(numberOfTasks++, description);
	}

	public static void reportTasks() {
		try {
			String text = "";

			Path progressLogFilePath = Paths.get(logFileProgressPrefix + String
				.valueOf(getRank()) + ".plog");
			
			Files.write(progressLogFilePath, Integer.toString(getSize()).concat(System.lineSeparator())
				.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

			for (Integer counter = 0; counter < numberOfTasks; counter++) {
				text = String.valueOf(counter).concat(",").concat(tasks.get(counter))
					.concat(System.lineSeparator());
				Files.write(progressLogFilePath, text.getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}

		}
		catch (IOException e) {
			LOGGER.warning("" + e.getMessage());
		}
	}

	public static int reportProgress(int taskId, int progress) {
		try {
			Path progressLogFilePath = Paths.get(logFileProgressPrefix + String
				.valueOf(getRank()) + ".plog");
			String text = String.valueOf(taskId).concat(",").concat(String.valueOf(
				progress)).concat(System.lineSeparator());
			Files.write(progressLogFilePath, text.getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException e) {
			LOGGER.warning("reportProgress error - " + e.getMessage());
			return -1;
		}
		return 0;
	}

	public static int reportText(String textToReport) {
		try {
			Files.write(Paths.get(logFileReportPrefix + String.valueOf(getRank()) +
				".tlog"), textToReport.concat(System.lineSeparator()).getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException e) {
			LOGGER.warning("reportText error - " + e.getMessage());
			return -1;
		}
		return 0;
	}

	public static int initialise() {
		String[] args = new String[1];
		args[0] = "ImageJ-linux64";
		try {
			MPI.Init(args);
			return 0;
		}
		catch (Exception e) {
			LOGGER.warning("MPI.Init() error - " + e.getMessage());
		}
		return -1;
	}

	public static int finalise() {
		try {
			MPI.Finalize();
			return 0;
		}
		catch (Exception e) {
			LOGGER.warning("MPI.Finalize() error - " + e.getMessage());
		}
		return -1;
	}

	public static int getRank() {
		int rank = -1;
		try {
			rank = MPI.COMM_WORLD.getRank();
		}
		catch (Exception e) {
			LOGGER.warning("MPI.COMM_WORLD.getRank() error - " + e.getMessage());
		}
		return rank;
	}

	public static int getSize() {
		int size = -1;
		try {
			size = MPI.COMM_WORLD.getSize();
		}
		catch (Exception e) {
			LOGGER.warning("MPI.COMM_WORLD.getSize() error - " + e.getMessage());
		}
		return size;
	}

	public static int barrier() {
		try {
			MPI.COMM_WORLD.barrier();
			return 0;
		}
		catch (Exception e) {
			LOGGER.warning("MPI.COMM_WORLD.barrier() error - " + e.getMessage());
		}
		return -1;
	}

	private MPIWrapper() {
		// Empty private constructor.
	}
}
