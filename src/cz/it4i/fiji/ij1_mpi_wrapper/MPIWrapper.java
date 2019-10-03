
package cz.it4i.fiji.ij1_mpi_wrapper;

import mpi.Datatype;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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

	private static Map<Integer, Integer> lastWrittenTaskPercentage =
		new HashMap<>();

	public static void addTask(String description) {
		tasks.put(numberOfTasks++, description);
	}

	public static void reportTasks() {
		try {
			String text = "";

			Path progressLogFilePath = Paths.get(logFileProgressPrefix + String
				.valueOf(getRank()) + ".plog");

			Files.write(progressLogFilePath, Integer.toString(getSize()).concat(System
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
			LOGGER.warning("" + exc.getMessage());
		}
	}

	public static int reportProgress(int taskId, int progress) {
		// Ignore impossible new progress percentages:
		if (progress > 100 || progress < 0) {
			return lastWrittenTaskPercentage.get(taskId);
		}

		// Do not write progress percentage that has already been written to avoid
		// writing gigantic progress log files:
		if (!lastWrittenTaskPercentage.containsKey(taskId) ||
			progress > lastWrittenTaskPercentage.get(taskId))
		{
			lastWrittenTaskPercentage.put(taskId, progress);

			try {
				Path progressLogFilePath = Paths.get(logFileProgressPrefix + String
					.valueOf(getRank()) + ".plog");
				String text = String.valueOf(taskId).concat(",").concat(String.valueOf(
					progress)).concat(System.lineSeparator());
				Files.write(progressLogFilePath, text.getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}
			catch (IOException exc) {
				LOGGER.warning("reportProgress error - " + exc.getMessage());
				return -1;
			}
		}
		return 0;
	}

	public static int reportText(String textToReport) {
		try {
			Files.write(Paths.get(logFileReportPrefix + String.valueOf(getRank()) +
				".tlog"), textToReport.concat(System.lineSeparator()).getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException exc) {
			LOGGER.warning("reportText error - " + exc.getMessage());
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
		catch (Exception exc) {
			LOGGER.warning("MPI.Init() error - " + exc.getMessage());
		}
		return -1;
	}

	public static int finalise() {
		try {
			MPI.Finalize();
			return 0;
		}
		catch (Exception exc) {
			LOGGER.warning("MPI.Finalize() error - " + exc.getMessage());
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
		catch (Exception exc) {
			LOGGER.warning("MPI.COMM_WORLD.barrier() error - " + exc.getMessage());
		}
		return -1;
	}

	private MPIWrapper() {
		// Empty private constructor to hide default public one.
	}

	// Details about macro variables at
	// https://imagej.nih.gov/ij/developer/macro/macros.html at the variables
	// section:
	private static Datatype findDatatype(Object sendBuffer) {
		Datatype buffersDatatype = MPI.DOUBLE;
		if (sendBuffer.getClass().equals(double[].class)) {
			buffersDatatype = MPI.DOUBLE;
		}
		else if (sendBuffer.getClass().equals(boolean[].class)) {
			buffersDatatype = MPI.BOOLEAN;
		}
		else if (sendBuffer.getClass().equals(char[].class)) {
			buffersDatatype = MPI.CHAR;
		}
		else {
			LOGGER.warning("Unknown type: " + sendBuffer.getClass()
				.getCanonicalName());
		}
		return buffersDatatype;
	}

	// Simple scatter which attempts to split the send buffer to equal parts among
	// the nodes:
	public static String scatterEqually(String sendString, int root) {
		// Convert comma separated string to array:
		double[] sendBuffer = convertCommaSeparatedStringToArray(sendString);

		int count = 0;
		// Divide work to equal parts:
		int totalLength = Array.getLength(sendBuffer);
		int part = totalLength / getSize();
		count = part;
		// Any additional remaining work should be given to rank 0:
		if (getRank() == 0) {
			int remainingWork = totalLength % getSize();
			count = part + remainingWork;
		}

		double[] receivedBuffer = (double[]) scatterArray(sendBuffer, count, count,
			root);

		// Convert back to string and return:
		return convertArrayToCommaSeparatedString(receivedBuffer);
	}

	public static String scatter(String sendString, int sendCount,
		int receiveCount, int root)
	{
		double[] sendBuffer = convertCommaSeparatedStringToArray(sendString);
		double[] receiveBuffer = (double[]) scatterArray(sendBuffer, sendCount,
			receiveCount, root);
		return convertArrayToCommaSeparatedString(receiveBuffer);
	}

	private static Object scatterArray(Object sendBuffer, int sendCount,
		int receiveCount, int root)
	{
		// The receive buffer will be of the same type as the send buffer:
		Object receiveBuffer = Array.newInstance(sendBuffer.getClass()
			.getComponentType(), receiveCount);

		try {
			Datatype sendType = findDatatype(sendBuffer);
			Datatype receiveType = sendType;

			MPI.COMM_WORLD.scatter(sendBuffer, sendCount, sendType, receiveBuffer,
				receiveCount, receiveType, root);
		}
		catch (MPIException exc) {
			LOGGER.warning(exc.getMessage());
		}
		return receiveBuffer;
	}

	private static String convertArrayToCommaSeparatedString(double[] array) {
		int length = array.length;
		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < length; i++) {
			bld.append(array[i]);
			if (i != length - 1) {
				bld.append(", ");
			}
		}
		return bld.toString();
	}

	private static double[] convertCommaSeparatedStringToArray(String string) {
		String[] arrayString = string.split(",");
		return Arrays.stream(arrayString).mapToDouble(Double::parseDouble)
			.toArray();
	}
}
