
package cz.it4i.fiji.parallel_macro.functions;

import java.lang.reflect.Array;

import ij.macro.MacroExtension;

public class PrintArray implements MyMacroExtensionDescriptor {

	@Override
	public void runFromMacro(Object[] parameters) {
		System.out.println(parameters[0].getClass().getCanonicalName());

		double[] myDoubleArray = null;
		// Convert to array:
		if (parameters[0].getClass().isArray()) {
			myDoubleArray = new double[Array.getLength(parameters[0])];
			System.out.println("Size : "+Array.getLength(parameters[0]));
			for (int i = 0; i < Array.getLength(parameters[0]); i++) {
				String str = ((Object[])parameters[0])[i].toString();
				myDoubleArray[i] = Double.parseDouble(str);
			}
		}

		// Print double array:
		if (myDoubleArray != null) {
			for (double element : myDoubleArray) {
				System.out.println("Double element: "+element);
			}
		}

	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_ARRAY };
	}

	@Override
	public String description() {
		return "Prints the contents of an array line by line. This function will be removed.";
	}

	@Override
	public String parameters() {
		return "An array of double.";
	}
}
