
package cz.it4i.fiji.parallel_macro;

import java.nio.DoubleBuffer;
import java.util.logging.Logger;

import mpi.Datatype;
import mpi.MPI;
import mpi.MPIException;

public class MPIParallelism implements Parallelism {

	private static MpiReflection mpiReflection;

	// Find and load the mpi.jar from OpenMPI:
	static {
		mpiReflection = new MpiReflection();
		String path = mpiReflection.findMpiJarFile();
		mpiReflection.loadOpenMpi(path);
	}

	private final Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	private ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();

	@Override
	public int initialise() {
		String[] arg0 = { "one", "two" };
		try {
			mpiReflection.initialise(arg0);
			return 0;
		}
		catch (Exception exc) {
			logger.warning("MPI.Init() error - " + exc.getMessage());
			return -1;
		}
	}

	@Override
	public int finalise() {
		try {
			mpiReflection.finalise();
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
			rank = mpiReflection.getRank();
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
			size = mpiReflection.getSize();
		}
		catch (Exception e) {
			logger.warning("MPI.COMM_WORLD.getSize() error - " + e.getMessage());
		}
		return size;
	}

	@Override
	public int barrier() {
		try {
			mpiReflection.barrier();
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
		// Convert comma separated string to double buffer:
		DoubleBuffer sendBuffer = converter.convertCommaSeparatedStringToBuffer(
			sendString);

		int size = getSize();
		int myRank = getRank();

		// Divide work to equal parts:
		int receiveCount = getEqualAmountOfWork(myRank, size,
			totalSendBufferLength);

		// The sender should send the correct amount of elements to each node:
		int[] sendCounts = new int[size];
		int[] displacements = new int[size];
		if (myRank == sender) {
			int sendCount = 0;
			int offset = 0;
			for (int destination = 0; destination < size; destination++) {
				sendCount = getEqualAmountOfWork(destination, size,
					totalSendBufferLength);
				sendCounts[destination] = sendCount;
				displacements[destination] = offset;
				offset += sendCount;
			}
		}

		DoubleBuffer receiveBuffer = MPI.newDoubleBuffer(receiveCount);
		try {
			MPI.COMM_WORLD.scatterv(sendBuffer, sendCounts, displacements, MPI.DOUBLE,
				receiveBuffer, receiveCount, MPI.DOUBLE, sender);
		}
		catch (MPIException exc) {
			logger.warning(exc.getMessage());
		}

		// Convert back to string and return:
		return converter.convertBufferToCommaSeparatedString(receiveBuffer,
			receiveCount);
	}

	@Override
	public String scatter(String sendString, int sendCount, int receiveCount,
		int root)
	{
		DoubleBuffer sendBuffer;
		if (!sendString.isEmpty()) {
			sendBuffer = converter.convertCommaSeparatedStringToBuffer(sendString);
		}
		else {
			sendBuffer = MPI.newDoubleBuffer(0);
		}

		DoubleBuffer receiveBuffer = scatterArray(sendBuffer, sendCount,
			receiveCount, root);
		return converter.convertBufferToCommaSeparatedString(receiveBuffer,
			receiveCount);
	}

	private DoubleBuffer scatterArray(DoubleBuffer sendBuffer, int sendCount,
		int receiveCount, int root)
	{
		// The receive buffer will be of the same type as the send buffer:
		DoubleBuffer receiveBuffer = MPI.newDoubleBuffer(receiveCount);

		try {
			Datatype sendType = MPI.DOUBLE;
			Datatype receiveType = sendType;

			MPI.COMM_WORLD.scatter(sendBuffer, sendCount, sendType, receiveBuffer,
				receiveCount, receiveType, root);
		}
		catch (MPIException exc) {
			logger.warning(exc.getMessage());
		}
		return receiveBuffer;
	}

	private DoubleBuffer gatherArray(DoubleBuffer sendBuffer, int sendCount,
		int receiveCount, int root)
	{
		// The receive buffer will be of the same type as the send buffer:
		DoubleBuffer receiveBuffer = null;
		// Only the specified node will gather the send items:
		if (getRank() == root) {
			receiveBuffer = MPI.newDoubleBuffer(receiveCount * getSize());
		}

		try {
			Datatype sendType = MPI.DOUBLE;
			Datatype receiveType = sendType;

			MPI.COMM_WORLD.gather(sendBuffer, sendCount, sendType, receiveBuffer,
				receiveCount, receiveType, root);
		}
		catch (MPIException exc) {
			logger.warning(exc.getMessage());
		}
		return receiveBuffer;
	}

	@Override
	public String gather(String sendString, int sendCount, int receiveCount,
		int root)
	{
		DoubleBuffer sendBuffer;
		if (!sendString.isEmpty()) {
			sendBuffer = converter.convertCommaSeparatedStringToBuffer(sendString);
		}
		else {
			sendBuffer = MPI.newDoubleBuffer(0);
		}

		DoubleBuffer receiveBuffer = gatherArray(sendBuffer, sendCount,
			receiveCount, root);
		return converter.convertBufferToCommaSeparatedString(receiveBuffer,
			receiveCount);
	}

	@Override
	public String gatherEqually(String sendString, int totalReceiveBufferLength,
		int receiver)
	{
		// Convert comma separated string to array:
		DoubleBuffer sendBuffer = converter.convertCommaSeparatedStringToBuffer(
			sendString);

		int size = getSize();

		int[] receiveCounts = new int[size];
		int[] displacements = new int[size];
		int sendCount = 0;
		int offset = 0;
		for (int destination = 0; destination < size; destination++) {
			sendCount = getEqualAmountOfWork(destination, size,
				totalReceiveBufferLength);
			receiveCounts[destination] = sendCount;
			displacements[destination] = offset;
			offset += sendCount;
		}

		DoubleBuffer receivedBuffer;
		if (getRank() == receiver) {
			receivedBuffer = MPI.newDoubleBuffer(totalReceiveBufferLength);
		}
		else {
			receivedBuffer = MPI.newDoubleBuffer(0);
		}

		try {
			MPI.COMM_WORLD.gatherv(sendBuffer, sendCount, MPI.DOUBLE, receivedBuffer,
				receiveCounts, displacements, MPI.DOUBLE, receiver);
		}
		catch (MPIException exc) {
			logger.warning(exc.getMessage());
		}

		// Convert back to string and return:
		if (getRank() == receiver) {
			return converter.convertBufferToCommaSeparatedString(receivedBuffer,
				totalReceiveBufferLength);
		}
		return "";
	}

	private int getEqualAmountOfWork(int myRank, int size, int totalSizeOfWork) {
		// Divide work to equal parts:
		int sizeOfWorkPart = totalSizeOfWork / size;

		// Any additional remaining items should be given to rank 0:
		if (myRank == 0) {
			sizeOfWorkPart += totalSizeOfWork % size;
		}
		return sizeOfWorkPart;
	}
}
