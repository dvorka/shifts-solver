package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeAllocation {

	public Employee employee;

	public int shiftsToGet;
	public int shifts;
	
	public EmployeeAllocation(Employee employee, int workdaysInPeriod) {
		this.employee=employee;
		this.shifts=0;
		
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)workdaysInPeriod)/8f*7.5f);
		} else {
			shiftsToGet=Math.round((((float)workdaysInPeriod)/8f*7.5f)/2f);			
		}
	}

	public void assign() {
		shifts++;
	}
	
	public boolean hasCapacity() {
		return shiftsToGet>0 && shiftsToGet>shifts;
	}
}
