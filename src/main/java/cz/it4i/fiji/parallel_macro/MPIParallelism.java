
package cz.it4i.fiji.parallel_macro;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Logger;

import mpi.Datatype;
import mpi.MPI;
import mpi.MPIException;

public class MPIParallelism implements Parallelism {

	private final Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	private ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();

	@Override
	public int initialise() {
		String[] args = new String[1];
		args[0] = "ImageJ-linux64";
		try {
			MPI.Init(args);
			return 0;
		}
		catch (Exception exc) {
			logger.warning("MPI.Init() error - " + exc.getMessage());
		}
		return -1;
	}

	@Override
	public int finalise() {
		try {
			MPI.Finalize();
			return 0;
		}
		catch (Exception exc) {
			logger.warning("MPI.Finalize() error - " + exc.getMessage());
		}
		return -1;
	}

	@Override
	public int getRank() {
		int rank = -1;
		try {
			rank = MPI.COMM_WORLD.getRank();
		}
		catch (Exception e) {
			logger.warning("MPI.COMM_WORLD.getRank() error - " + e.getMessage());
		}
		return rank;
	}

	@Override
	public int getSize() {
		int size = -1;
		try {
			size = MPI.COMM_WORLD.getSize();
		}
		catch (Exception e) {
			logger.warning("MPI.COMM_WORLD.getSize() error - " + e.getMessage());
		}
		return size;
	}

	@Override
	public int barrier() {
		try {
			MPI.COMM_WORLD.barrier();
			return 0;
		}
		catch (Exception exc) {
			logger.warning("MPI.COMM_WORLD.barrier() error - " + exc.getMessage());
		}
		return -1;
	}

	// Simple scatter which attempts to split the send buffer to equal parts among
	// the nodes:
	@Override
	public String scatterEqually(String sendString, int totalSendBufferLength,
		int sender)
	{
		// Convert comma separated string to array:
		double[] sendBuffer;
		if (!sendString.isEmpty()) {
			sendBuffer = converter.convertCommaSeparatedStringToArray(sendString);
		}
		else {
			sendBuffer = new double[0];
		}

		int size = getSize();

		// Divide work to equal parts:
		int receiveCount = totalSendBufferLength / size;

		// Any additional remaining items should be given to rank 0:
		if (getRank() == 0) {
			receiveCount += totalSendBufferLength % size;
		}

		int tag = 99;
		Datatype datatype = findDatatype(sendBuffer);
		
		// The sender should send the correct amount of elements to each node:
		if (getRank() == sender) {
			int offset = 0;
			int sendCount = 0;
			for (int destination = 0; destination < size; destination++) {
				sendCount = totalSendBufferLength / size;
				if (destination == 0) {
					sendCount += totalSendBufferLength % size;
				}
				try {
					double[] newSendBuffer = Arrays.copyOfRange(sendBuffer, offset, sendBuffer.length);
					MPI.COMM_WORLD.send(newSendBuffer, sendCount, datatype, destination,
						tag);
				}
				catch (MPIException exc) {
					logger.warning(exc.getMessage());
				}
				offset += sendCount;
			}
		}

		// Receive your share of the elements:
		double[] receivedBuffer = new double[receiveCount];
		try {
			MPI.COMM_WORLD.recv(receivedBuffer, receiveCount, datatype, sender, tag);
		}
		catch (MPIException exc) {
			logger.warning(exc.getMessage());
		}

		// Convert back to string and return:
		return converter.convertArrayToCommaSeparatedString(receivedBuffer);
	}

	@Override
	public String scatter(String sendString, int sendCount, int receiveCount,
		int root)
	{
		double[] sendBuffer;
		if (!sendString.isEmpty()) {
			sendBuffer = converter.convertCommaSeparatedStringToArray(sendString);
		}
		else {
			sendBuffer = new double[0];
		}

		double[] receiveBuffer = (double[]) scatterArray(sendBuffer, sendCount,
			receiveCount, root);
		return converter.convertArrayToCommaSeparatedString(receiveBuffer);
	}

	// Details about macro variables at
	// https://imagej.nih.gov/ij/developer/macro/macros.html at the variables
	// section:
	private Datatype findDatatype(Object sendBuffer) {
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
			logger.warning("Unknown type: " + sendBuffer.getClass()
				.getCanonicalName());
		}
		return buffersDatatype;
	}

	private Object scatterArray(Object sendBuffer, int sendCount,
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
			logger.warning(exc.getMessage());
		}
		return receiveBuffer;
	}
}
