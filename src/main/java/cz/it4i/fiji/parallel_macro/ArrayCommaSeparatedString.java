
package cz.it4i.fiji.parallel_macro;

import java.nio.DoubleBuffer;
import java.util.regex.Pattern;

public class ArrayCommaSeparatedString {

	public String convertBufferToCommaSeparatedString(DoubleBuffer buffer,
		int length)
	{
		if (buffer != null) {
			StringBuilder bld = new StringBuilder();
			for (int i = 0; i < length; i++) {
				bld.append(buffer.get(i));
				if (i != length - 1) {
					bld.append(", ");
				}
			}
			return bld.toString();
		}
		return "";
	}

	public DoubleBuffer convertCommaSeparatedStringToBuffer(String string,
		MpiReflection mpiReflection)
	{
		Pattern pattern = Pattern.compile(",");
		double[] tempArray = pattern.splitAsStream(string).mapToDouble(
			Double::parseDouble).toArray();
		DoubleBuffer tempBuffer = mpiReflection.newDoubleBuffer(tempArray.length);
		for (int i = 0; i < tempArray.length; i++) {
			tempBuffer.put(tempArray[i]);
		}
		return tempBuffer;
	}
}
