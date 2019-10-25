
package cz.it4i.fiji.parallel_macro.functions;

import cz.it4i.fiji.parallel_macro.ParallelMacro;
import ij.macro.MacroExtension;

public class ReportText implements MyMacroExtensionDescriptor {

	private void actualAlgorithm(String textToReport) {
		ParallelMacro.reportText(textToReport);
	}

	@Override
	public void runFromMacro(Object[] parameters) {
		String textToReport = (String) parameters[0];
		actualAlgorithm(textToReport);
	}

	@Override
	public int[] parameterTypes() {
		return new int[] { MacroExtension.ARG_STRING };
	}

	@Override
	public String description() {
		return "Outputs given text to the node's log.";
	}

	@Override
	public String parameters() {
		return "text";
	}
}
