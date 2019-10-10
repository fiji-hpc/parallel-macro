
package cz.it4i.fiji.ij1_mpi_wrapper;

import java.util.Arrays;

public class ArrayCommaSeparatedString {

	public String convertArrayToCommaSeparatedString(double[] array) {
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

	public double[] convertCommaSeparatedStringToArray(String string) {
		String[] arrayString = string.split(",");
		return Arrays.stream(arrayString).mapToDouble(Double::parseDouble)
			.toArray();
	}
}
