package com.mindforger.shiftsolver.client.solver;

import java.util.List;

public class ShiftSolverException extends RuntimeException {
	private static final long serialVersionUID = -4150503874248963534L;
	
	private String message;
	private int failedOnDay;
	private int failedOnMaxDepth;
	private String failedOnShiftType;
	private String failedOnRole;
	private List<EmployeeAllocation> failedOnEmloyeeAllocations;

	public ShiftSolverException(
			String message,
			List<EmployeeAllocation> failedOnEmployeeAllocations, 
			int failedOnDay,
			int failedOnMaxDepth,
			String failedOnShiftType, 
			String failedOnRole) 
	{
		super(message);
		this.message=message;
		this.failedOnEmloyeeAllocations=failedOnEmployeeAllocations;
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

	public int getFailedOnDay() {
		return failedOnDay;
	}

	public void setFailedOnDay(int failedOnDay) {
		this.failedOnDay = failedOnDay;
	}

	public int getFailedOnMaxDepth() {
		return failedOnMaxDepth;
	}

	public void setFailedOnMaxDepth(int failedOnMaxDepth) {
		this.failedOnMaxDepth = failedOnMaxDepth;
	}

	public String getFailedOnShiftType() {
		return failedOnShiftType;
	}

	public void setFailedOnShiftType(String failedOnShiftType) {
		this.failedOnShiftType = failedOnShiftType;
	}

	public String getFailedOnRole() {
		return failedOnRole;
	}

	public void setFailedOnRole(String failedOnRole) {
		this.failedOnRole = failedOnRole;
	}

	public List<EmployeeAllocation> getFailedOnEmloyeeAllocations() {
		return failedOnEmloyeeAllocations;
	}

	public void setFailedOnEmloyeeAllocations(
			List<EmployeeAllocation> failedOnEmloyeeAllocations) {
		this.failedOnEmloyeeAllocations = failedOnEmloyeeAllocations;
	}
}
