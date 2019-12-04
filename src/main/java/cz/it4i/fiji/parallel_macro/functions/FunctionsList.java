
package cz.it4i.fiji.parallel_macro.functions;

public class FunctionsList {

	// macro extensions should have a prefix in order to prevent
	// conflicts between different extensions
	public static final String MACRO_EXTENSION_PREFIX = "par";

	// list of all available functions
	private static MyMacroExtensionDescriptor[] list = { new Initialise(),
		new ReportText(), new Finalise(), new Flip(), new Set(),
		new AddImageAndScalar(), new Equal() };

	public static MyMacroExtensionDescriptor[] getList() {
		return list;
	}

	private FunctionsList() {
		// This is a private constructor to hide the implicit public one.
	}
}
