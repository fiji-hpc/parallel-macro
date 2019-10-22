
package cz.it4i.fiji.ij1_mpi_wrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class TextReportLogging {

	private Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	private static final String LOG_FILE_REPORT_PREFIX = "report_";
	private static final String LOG_FILE_REPORT_POSTFIX = ".tlog";

	public int reportText(String textToReport, int rank) {
		try {
			Files.write(Paths.get(LOG_FILE_REPORT_PREFIX + String.valueOf(rank) +
				LOG_FILE_REPORT_POSTFIX), textToReport.concat(System.lineSeparator())
					.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException exc) {
			logger.warning("reportText error - " + exc.getMessage());
			return -1;
		}
		return 0;
	}
}
