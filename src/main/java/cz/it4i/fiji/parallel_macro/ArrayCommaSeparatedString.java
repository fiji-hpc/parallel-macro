
package cz.it4i.fiji.parallel_macro;

import java.util.regex.Pattern;

public class ArrayCommaSeparatedString {

	public String convertArrayToCommaSeparatedString(double[] array) {
		if (array != null) {
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
		return "";
	}

	public double[] convertCommaSeparatedStringToArray(String string) {
		Pattern pattern = Pattern.compile(",");
		return pattern.splitAsStream(string).mapToDouble(Double::parseDouble)
			.toArray();
	}
}
