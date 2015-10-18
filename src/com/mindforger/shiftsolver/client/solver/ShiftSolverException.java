package com.mindforger.shiftsolver.client.solver;

public class ShiftSolverException extends RuntimeException {
	private static final long serialVersionUID = -4150503874248963534L;
	
	private String message;
	private int failedOnDay;
	private int failedOnMaxDepth;
	private String failedOnShiftType;
	private String failedOnRole;

	public ShiftSolverException(String message, int failedOnDay, int failedOnMaxDepth, String failedOnShiftType, String failedOnRole) {
		super(message);
		this.message=message;
		this.failedOnDay=failedOnDay;
		this.failedOnMaxDepth=failedOnMaxDepth;
		this.failedOnShiftType=failedOnShiftType;
		this.failedOnRole=failedOnRole;
	}

	@Override
	public String getMessage() {
		return message + 
				" ("+failedOnDay+"-"+failedOnShiftType+"-"+failedOnRole+"-"+failedOnMaxDepth+")";

	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}
}
