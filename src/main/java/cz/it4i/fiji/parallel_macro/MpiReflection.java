
package cz.it4i.fiji.parallel_macro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

public class MpiReflection {

	private Method mpiInit;
	private Method mpiFinalize;
	private Method mpiBarrier;
	private Method mpiGetRank;
	private Method mpiGetSize;
	private Method mpiNewDoubleBuffer;
	private Method mpiScatterv;
	private Method mpiScatter;
	private Method mpiGather;
	private Method mpiGatherv;

	private Object mpiInstance;
	private Object commWorldInstance;
	public Object mpiDoubleInstance;

	public String findMpiJarFile() {
		String script = "";
		String jarPath = "";
		try {
			// Read script file:
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				MPIParallelism.class.getClassLoader().getResourceAsStream(
					"findMpiJar.sh")));
			String line = reader.readLine();
			StringBuilder bld = new StringBuilder();
			while (line != null) {
				bld.append(line + "\n");
				line = reader.readLine();
			}
			script = bld.toString();

		}
		catch (IOException exc1) {
			System.out.println("Could not read script in resources!");
			exc1.printStackTrace();
		}

		// Write the script file:
		try (PrintWriter out = new PrintWriter(System.getProperty("user.home") +
			"/findMpiJar.sh"))
		{
			out.println(script);
		}
		catch (FileNotFoundException exc1) {
			System.out.println("Could not copy file.");
			exc1.printStackTrace();
		}

		System.out.println("Read the script:\n" + script);

		try {
			// Execute the script:
			String createAndRunCommand = "/bin/sh $HOME/findMpiJar.sh;";
			Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c",
				createAndRunCommand });

			// Read the output of the script:
			BufferedReader output = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
			List<String> files = new ArrayList<>();
			String line = "";
			System.out.println("Output of the command.");
			while ((line = output.readLine()) != null) {
				files.add(line);
				System.out.println(line + "\n");
			}

			// Read the error of the script:
			BufferedReader error = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
			while ((line = error.readLine()) != null) {
				System.out.println(line + "\n");
			}

			// Dynamically link the files.
			if (!files.isEmpty()) {
				jarPath = files.get(0);
				System.out.println("Dynamically load from: " + jarPath);
			}
			else {
				System.out.println("No OpenMPI was found on your system." +
					" Please install OpenMPI before using Parallel-Macro.");
				System.exit(0);
			}
		}
		catch (IOException exc) {
			System.out.println("OpenMPI's mpi.jar was not found on this system.");
			exc.printStackTrace();
			System.exit(0);
		}

		return jarPath;
	}

	public void loadOpenMpi(String path) {
		System.out.println("The path of MPI.jar is: " + path);
		try {
			URLClassLoader child;
			child = new URLClassLoader(new URL[] { new URL("file://" + path) },
				MPIParallelism.class.getClassLoader());
			Class<?> mpiClass = Class.forName("mpi.MPI", true, child);
			mpiInstance = mpiClass.newInstance();

			// Methods:
			mpiInit = mpiClass.getDeclaredMethod("Init", String[].class);
			mpiFinalize = mpiClass.getDeclaredMethod("Finalize");
			Field commWorld = mpiClass.getDeclaredField("COMM_WORLD");
			Field mpiDoubleField = mpiClass.getDeclaredField("DOUBLE");
			Class<?> mpiDoubleClass = mpiDoubleField.getType();
			mpiDoubleInstance = mpiDoubleField.get(mpiInstance);
			commWorldInstance = commWorld.get(mpiInstance);
			mpiBarrier = commWorldInstance.getClass().getMethod("barrier",
				new Class[] {});
			mpiGetRank = commWorldInstance.getClass().getMethod("getRank",
				new Class[] {});
			mpiGetSize = commWorldInstance.getClass().getMethod("getSize",
				new Class[] {});
			mpiNewDoubleBuffer = mpiClass.getMethod("newDoubleBuffer", int.class);

			mpiScatterv = commWorldInstance.getClass().getMethod("scatterv",
				Object.class, int[].class, int[].class, mpiDoubleClass, Object.class,
				int.class, mpiDoubleClass, int.class);

			mpiScatter = commWorldInstance.getClass().getMethod("scatter",
				Object.class, int.class, mpiDoubleClass, Object.class, int.class,
				mpiDoubleClass, int.class);
			mpiGather = commWorldInstance.getClass().getMethod("gather", Object.class,
				int.class, mpiDoubleClass, Object.class, int.class, mpiDoubleClass,
				int.class);
			mpiGatherv = commWorldInstance.getClass().getMethod("gatherv",
				Object.class, int.class, mpiDoubleClass, Object.class, int[].class,
				int[].class, mpiDoubleClass, int.class);
		}
		catch (MalformedURLException | ClassNotFoundException
				| NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InstantiationException
				| NoSuchFieldException exc)
		{
			exc.printStackTrace();
		}
	}

	public void initialise(String[] arg0) throws IllegalAccessException,
		IllegalArgumentException, InvocationTargetException
	{
		Object[] args = { arg0 };
		mpiInit.invoke(mpiInstance, args);
	}

	public void finalise() throws IllegalAccessException,
		IllegalArgumentException, InvocationTargetException
	{
		mpiFinalize.invoke(mpiInstance);
	}

	public int getRank() throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		Object tempRank = mpiGetRank.invoke(commWorldInstance);
		return (int) tempRank;
	}

	public int getSize() throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		Object tempSize = mpiGetSize.invoke(commWorldInstance);
		return (int) tempSize;
	}

	public void barrier() throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		mpiBarrier.invoke(commWorldInstance);
	}

	public DoubleBuffer newDoubleBuffer(int size) {
		Object temp;
		try {
			temp = mpiNewDoubleBuffer.invoke(mpiInstance, size);
		}
		catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException exc)
		{
			// In case of error allocate a 0 size double buffer.
			return DoubleBuffer.allocate(0);
		}
		return (DoubleBuffer) temp;
	}

	public void scatterv(Object sendBuffer, int[] sendCounts, int[] displacements,
		Object mpiSendDataType, Object receiveBuffer, int receiveCount,
		Object mpiReceiveDataType, int sender) throws IllegalAccessException,
		IllegalArgumentException, InvocationTargetException
	{
		mpiScatterv.invoke(commWorldInstance, sendBuffer, sendCounts, displacements,
			mpiSendDataType, receiveBuffer, receiveCount, mpiReceiveDataType, sender);
	}

	public void scatter(Object sendBuffer, int sendCount, Object mpiSendDataType,
		Object receiveBuffer, int receiveCount, Object mpiReceiveDataType, int root)
		throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		mpiScatter.invoke(commWorldInstance, sendBuffer, sendCount, mpiSendDataType,
			receiveBuffer, receiveCount, mpiReceiveDataType, root);
	}

	public void gather(Object sendBuffer, int sendCount, Object mpiSendDataType,
		Object receiveBuffer, int receiveCount, Object mpiReceiveDataType, int root)
		throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		mpiGather.invoke(commWorldInstance, sendBuffer, sendCount, mpiSendDataType,
			receiveBuffer, receiveCount, mpiReceiveDataType, root);
	}

	public void gatherv(Object sendBuffer, int sendCount, Object mpiSendDataTypes,
		Object receivedBuffer, int[] receiveCounts, int[] displacements,
		Object mpiReceiveDataTypes, int receiver) throws IllegalAccessException,
		IllegalArgumentException, InvocationTargetException
	{
		mpiGatherv.invoke(sendBuffer, sendCount, mpiSendDataTypes, receivedBuffer,
			receiveCounts, displacements, mpiReceiveDataTypes, receiver);
	}
}
