
package cz.it4i.fiji.parallel_macro;

import java.util.Arrays;

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
		if(!string.isEmpty()) {
		String[] arrayString = string.split(",");
		return Arrays.stream(arrayString).mapToDouble(Double::parseDouble)
			.toArray();
		}
		return new double[0];
	}
}
