package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.shared.model.shifts.ShiftSolution;

public class EmployeeSolution implements Serializable  {
	private static final long serialVersionUID = -530359598396424484L;

	private Employee employee;
	private List<ShiftSolution> shifts;
	
	public EmployeeSolution() {
		shifts=new ArrayList<ShiftSolution>();
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<ShiftSolution> getShifts() {
		return shifts;
	}

	public void setShifts(List<ShiftSolution> shifts) {
		this.shifts = shifts;
	}
}
