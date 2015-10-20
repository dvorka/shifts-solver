package com.mindforger.shiftsolver.shared;

import java.io.PrintStream;

public class ShiftSolverLogger {

	private static PrintStream out;
	
	static {
		// nop
		out=null;		
	}
	
	public static void init(PrintStream printStream) {
		out=printStream;
	}
	
	public static void debug(String message) {
		//GWT.log(message);
		if(out!=null) out.println(message);
	}
}
