import mpi.MPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MPIWrapper {

    static String logFileProgressPrefix = "progress_";
    static String logFileReportPrefix = "report_";

    public static int reportProgress(int progress) {
        try {
            Files.write(Paths.get(logFileProgressPrefix + String.valueOf(getRank()) + ".plog"),
                    String.valueOf(progress).concat(System.lineSeparator()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("reportProgress error - " + e.getMessage());
            return -1;
        }
        return 0;
    }

    public static int reportText(String textToReport) {
        try {
            Files.write(Paths.get(logFileReportPrefix + String.valueOf(getRank()) + ".tlog"),
                    textToReport.concat(System.lineSeparator()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("reportText error - " + e.getMessage());
            return -1;
        }
        return 0;
    }


    public static int Init() {
        String args[] = new String[1];
        args[0] = "ImageJ-linux64";
        try {
            MPI.Init(args);
            return 0;
        } catch (Exception e) {
            System.err.println("MPI.Init() error - " + e.getMessage());
        }
        return -1;
    }

    public static int Finalize() {
        try {
            MPI.Finalize();
            return 0;
        } catch (Exception e) {
            System.err.println("MPI.Finalize() error - " + e.getMessage());
        }
        return -1;
    }

    public static int getRank() {
        int rank = -1;
        try {
            rank = MPI.COMM_WORLD.getRank();
        } catch (Exception e) {
            System.err.println("MPI.COMM_WORLD.getRank() error - " + e.getMessage());
        }
        return rank;
    }

    public static int getSize() {
        int size = -1;
        try {
            size = MPI.COMM_WORLD.getSize();
        } catch (Exception e) {
            System.err.println("MPI.COMM_WORLD.getSize() error - " + e.getMessage());
        }
        return size;
    }

    public static int barrier() {
        try {
            MPI.COMM_WORLD.barrier();
            return 0;
        } catch (Exception e) {
            System.err.println("MPI.COMM_WORLD.barrier() error - " + e.getMessage());
        }
        return -1;
    }

}
