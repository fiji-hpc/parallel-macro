
package cz.it4i.fiji.parallel_macro.functions;

public interface MyMacroExtensionDescriptor {

	void runFromMacro(Object[] parameters);

	int[] parameterTypes();

	String description();

	String parameters();
}
