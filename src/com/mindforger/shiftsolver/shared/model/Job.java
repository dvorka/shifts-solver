package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;

public class Job implements Serializable {
	private static final long serialVersionUID = 5771885426390872948L;
	
	public String key;
	public int shiftsLimit;
	public int shifts;
	
	public Job() {		
	}

	public Job(int shifts, int shiftsLimit) {
		this.shifts=shifts;
		this.shiftsLimit=shiftsLimit;
	}

	public int getShiftsLimit() {
		return shiftsLimit;
	}

	public void setShiftsLimit(int shiftsLimit) {
		this.shiftsLimit = shiftsLimit;
	}

	public int getShifts() {
		return shifts;
	}

	public void setShifts(int shifts) {
		this.shifts = shifts;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
