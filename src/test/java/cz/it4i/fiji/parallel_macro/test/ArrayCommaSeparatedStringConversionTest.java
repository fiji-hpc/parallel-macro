
package cz.it4i.fiji.parallel_macro.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.it4i.fiji.parallel_macro.ArrayCommaSeparatedString;

public class ArrayCommaSeparatedStringConversionTest {

	@Test
	public void ArrayToCommaSeparatedStringShouldWork() {
		double[] originalArray = { 10.0, 20.0, 30.0 };

		ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();
		String convertedString = converter.convertArrayToCommaSeparatedString(
			originalArray);
		assertEquals("10.0, 20.0, 30.0", convertedString);
	}

	@Test
	public void CommaSeparatedStringToArrayShouldWork() {
		String originalString = "40.0, 50.0, 60.0, 70.0";
		double[] expectedArray = { 40.0, 50.0, 60.0, 70.0 };

		ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();
		double[] convertedArray = converter.convertCommaSeparatedStringToArray(
			originalString);

		assertArrayEquals(expectedArray, convertedArray, 0.0);
	}

}
