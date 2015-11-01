package com.mindforger.shiftsolver.shared;

import java.io.PrintStream;

import com.google.gwt.core.client.GWT;

public class ShiftSolverLogger implements ShiftSolverConstants {

	private static PrintStream out;
	
	static {
		// nop
		out=null;		
	}
	
	public static void init(PrintStream printStream) {
		out=printStream;
	}
	
	public static void debug(String message) {
		if(DEBUG) {
			GWT.log(message);			
		}
		if(out!=null) out.println(message);
	}

	public static boolean isJUnitMode() {
		return out!=null;
	}
}
