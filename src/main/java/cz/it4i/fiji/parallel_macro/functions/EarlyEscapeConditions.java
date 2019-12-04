
package cz.it4i.fiji.parallel_macro.functions;

public class EarlyEscapeConditions {

	interface EarlyEscapeCondition {

		boolean escape(Object[] parameters);
	}

	public static final EarlyEscapeCondition flip = (Object[] parameters) -> {
		boolean flipX = (((double) parameters[2]) == 1.0);
		boolean flipY = (((double) parameters[3]) == 1.0);
		return (!flipX && !flipY);
	};

	public static final EarlyEscapeCondition set = (Object[] parameters) -> {
		return false;
	};

	public static final EarlyEscapeCondition addImageAndScalar = (
		Object[] parameters) -> {
		int value = (int) ((double) parameters[2]);
		// If the value to be added is zero the image will be the same. No need to
		// loop through the entire image.
		return value == 0;
	};

	public static final EarlyEscapeCondition equal = (Object[] parameters) -> {
		return false;
	};
}
