package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeAllocation {

	public Employee employee;

	public int shiftsToGet;
	public int shiftsCount;
	
	public EmployeeAllocation(Employee employee, int workdaysInPeriod) {
		this.employee=employee;
		this.shiftsCount=0;
		
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)workdaysInPeriod)/8f*7.5f);
		} else {
			shiftsToGet=Math.round((((float)workdaysInPeriod)/8f*7.5f)/2f);			
		}
	}

	public void assign() {
		shiftsCount++;
	}
	
	public boolean hasCapacity() {
		return true;
		// TODO infinite ;) return shiftsToGet>0 && shiftsToGet<shiftsCount;
	}
}
