
package cz.it4i.fiji.parallel_macro.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.it4i.fiji.parallel_macro.ArrayCommaSeparatedString;

public class ArrayCommaSeparatedStringConversionTest {

	@Test
	public void arrayToCommaSeparatedStringShouldWork() {
		double[] originalArray = { 10.0, 20.0, 30.0 };

		ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();
		String convertedString = converter.convertArrayToCommaSeparatedString(
			originalArray);
		assertEquals("10.0, 20.0, 30.0", convertedString);
	}

	@Test
	public void commaSeparatedStringToArrayShouldWork() {
		String originalString = "40.0, 50.0, 60.0, 70.0";
		double[] expectedArray = { 40.0, 50.0, 60.0, 70.0 };

		ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();
		double[] convertedArray = converter.convertCommaSeparatedStringToArray(
			originalString);

		assertArrayEquals(expectedArray, convertedArray, 0.0);
	}
	
	@Test
	public void conversionsShouldWorkOnBigArraysAndStrings() {
		//int size = 2816*2112; // size of the sample cake image.
		int size = 9000*3500; // size of the sample NASA Saturn image.
		double[] myBigArray= new double[size];
		for(int i = 0; i < myBigArray.length; i++) {
			myBigArray[i] = 255;
		}
		
		ArrayCommaSeparatedString converter = new ArrayCommaSeparatedString();
		String myBigString = converter.convertArrayToCommaSeparatedString(myBigArray);
		double[] myRecreatedBigArray = converter.convertCommaSeparatedStringToArray(myBigString);
		
		assertArrayEquals(myBigArray, myRecreatedBigArray, 0.0);
	}

}
