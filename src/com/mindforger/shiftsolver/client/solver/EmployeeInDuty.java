package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeInDuty {

	Employee employee;

	int shiftsToGet;
	int shiftsCount;
	
	public EmployeeInDuty(Employee employee, int workdaysInPeriod) {
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)workdaysInPeriod)*8f/7.5f);
		} else {
			shiftsToGet=Math.round((((float)workdaysInPeriod)*8f/7.5f)/2f);			
		}
	}
}
