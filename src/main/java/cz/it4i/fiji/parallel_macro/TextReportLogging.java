
package cz.it4i.fiji.parallel_macro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextReportLogging {

	private static final String LOG_FILE_REPORT_PREFIX = "report_";
	private static final String LOG_FILE_REPORT_POSTFIX = ".tlog";

	public int reportText(String textToReport, int rank) {
		try {
			Files.write(Paths.get(LOG_FILE_REPORT_PREFIX + String.valueOf(rank) +
				LOG_FILE_REPORT_POSTFIX), textToReport.concat(System.lineSeparator())
					.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException exc) {
			log.error("Report text error: {} ", exc.getMessage());
			return -1;
		}
		return 0;
	}
}
