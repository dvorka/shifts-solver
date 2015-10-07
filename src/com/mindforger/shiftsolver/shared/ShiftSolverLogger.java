package com.mindforger.shiftsolver.shared;

import java.io.PrintStream;

public class ShiftSolverLogger {

	private static PrintStream out;
	
	static {
		// nop
		out=null;
		
		// stdout
		//out=System.out;
		
		// file
//		try {
//			out=new PrintStream(new File("/tmp/s2.txt"));
//		} catch (FileNotFoundException e) {
//			System.err.println(e.getMessage());
//		}
	}
	
	public static void debug(String message) {
		if(out!=null) out.println(message);
	}
}
