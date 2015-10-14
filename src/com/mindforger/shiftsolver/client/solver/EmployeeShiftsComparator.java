package com.mindforger.shiftsolver.client.solver;

import java.util.Comparator;
import java.util.Map;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeShiftsComparator implements Comparator<Employee> {

	private Map<String, EmployeeAllocation> employeeAllocations;

	public EmployeeShiftsComparator( Map<String, EmployeeAllocation> employeeAllocations) {
		this.employeeAllocations=employeeAllocations;
	}

	@Override
	public int compare(Employee o1, Employee o2) {
		// TODO debug this: full > parts (every group sorted by shifts)
		if(o1.isFulltime()) {
			if(o2.isFulltime()) {
				return compareByShifts(o1, o2);				
			} else {
				return -1;
			}
		} else {
			if(o2.isFulltime()) {
				return 1;
			} else {
				return compareByShifts(o1, o2);				
			}			
		}
	}

	private int compareByShifts(Employee o1, Employee o2) {
		if(employeeAllocations.get(o1.getKey()).shifts>employeeAllocations.get(o2.getKey()).shifts) {
			return 1;
		} else {
			if(employeeAllocations.get(o1.getKey()).shifts<employeeAllocations.get(o2.getKey()).shifts) {
				return -1;
			} else {
				return 0;				
			}			
		}
	}
}
