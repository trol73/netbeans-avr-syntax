/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.trolsoft.avrasmext.lexer.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author trol
 */
public class Keywords {
	private static final Set<String> KEYWORDS = set(
			  "if", "else", "goto", "loop", "break", "continue", "as",
			  "ptr", "byte", "word", "dword", "prgptr"
	);
	private static final Set<String> DIRECTIVES = setCi(
			  ".proc", ".endproc", ".use", ".args", ".extern",
			  	".byte",	".cseg",	".csegsize", ".db", ".def", ".device",	".dseg",	".dw", ".endm", ".endmacro",
				".equ", ".eseg", ".exit", ".include", ".includepath",	".list",	".listmac",	".macro", ".nolist",
				".org", ".set", ".define", ".undef", ".ifdef", ".ifndef", ".if", ".else", ".elseif", ".elif",
				".endif", ".message", ".warning", ".error", ".pragma",

			   // GCC
			   ".global", ".section", ".text", ".ascii", ".asciz", ".data"
	);
	private static final Set<String> C_PREPROCESSOR = set(
			  "#if", "#ifdef", "#ifndef", "#else", "#elif", "#endif", "#define", "#undef", "#include", "#line", "#ident",
			  "#pragma", "#warning", "#error"
	);
	private static final Set<String> FLAGS = set("F_GLOBAL_INT", "F_BIT_COPY", "F_HALF_CARRY", "F_SIGN", "F_TCO",
																"F_NEG", "F_ZERO", "F_CARRY");
	private static final Set<String> ARRAYS = set("ram", "prg", "io");

	private static final Set<String> BUILTINS = set(
		// AVRA
		"LOW", "HIGH", "BYTE2", "BYTE3", "BYTE4", "LWRD", "HWRD", "PAGE", "EXP2", "LOG2",
		// GCC
		"lo8", "hi8", "hh8", "hlo8", "hhi8", "pm_lo8", "pm_hi8", "pm_hh8", "pm", "_SFR_IO_ADDR"
	);




	 private static Set<String> set(String ...s) {
		 Set<String> result = new HashSet<>();
		 result.addAll(Arrays.asList(s));
		 return result;
	 }

	 private static Set<String> setCi(String ...s) {
		 Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		 result.addAll(Arrays.asList(s));
		 return result;
	 }



	 public static boolean isKeyword(String s) {
		 return KEYWORDS.contains(s);
	 }

	 public static boolean isDirective(String s) {
		 return DIRECTIVES.contains(s);
	 }

	 public static boolean isFlag(String s) {
		 return FLAGS.contains(s);
	 }

	 public static boolean isArray(String s) {
		 return ARRAYS.contains(s);
	 }

	 public static boolean isPreprocessor(String s) {
		 return C_PREPROCESSOR.contains(s);
	 }

	 public static boolean isBuiltIn(String s) {
		 return BUILTINS.contains(s);
	 }
}
