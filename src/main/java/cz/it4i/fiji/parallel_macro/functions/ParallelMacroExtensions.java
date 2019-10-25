
package cz.it4i.fiji.parallel_macro.functions;

import ij.macro.Functions;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>ParallelMacroExtensions")
public class ParallelMacroExtensions implements MacroExtension, Command {

	@Override
	public String handleExtension(String name, Object[] args) {
		MyMacroExtensionDescriptor[] functionList = MyFunctions.list;

		// go through function list an check if we know the called function
		for (MyMacroExtensionDescriptor function : functionList) {
			String command = MyFunctions.MACRO_EXTENSION_PREFIX + function.getClass()
				.getSimpleName();

			// if name matches exactly
			if (command.compareTo(name) == 0) {
				// call the function
				function.runFromMacro(args);
				break;
			}
		}
		return null;
	}

	@Override
	public ExtensionDescriptor[] getExtensionFunctions() {
		MyMacroExtensionDescriptor[] pluginList = MyFunctions.list;

		ExtensionDescriptor[] result = new ExtensionDescriptor[pluginList.length];

		int i = 0;
		// formulate a list of ExtensionDescriptors describing all command this
		// class can handle
		for (MyMacroExtensionDescriptor function : pluginList) {
			String call = MyFunctions.MACRO_EXTENSION_PREFIX + function.getClass()
				.getSimpleName();
			result[i] = new ExtensionDescriptor(call, function.parameterTypes(),
				this);
			i++;
		}

		// hand over the list to ImageJs macro interpreter
		return result;
	}

	@Override
	public void run() {
		// Activate this class as handler for macro extensions
		Functions.registerExtensions(this);
	}
}
