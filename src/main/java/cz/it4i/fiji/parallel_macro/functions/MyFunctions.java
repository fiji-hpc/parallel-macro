package cz.it4i.fiji.parallel_macro.functions;

import cz.it4i.fiji.parallel_macro.functions.MyMacroExtensionDescriptor;

public interface MyFunctions {
    // macro extensions should have a prefix in order to prevent
    // conflicts between different extensions
    String MACRO_EXTENSION_PREFIX = "par";

    // list of all available functions
    MyMacroExtensionDescriptor[] list = {
            new ReportText()
    };
}
