package com.mindforger.shiftsolver.client.solver;

import java.util.Comparator;
import java.util.Map;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeFulltimeYesterdayAndShiftsComparator implements Comparator<Employee> {

	private Map<String, EmployeeAllocation> employeeAllocations;
	private int today;

	public EmployeeFulltimeYesterdayAndShiftsComparator(Map<String, EmployeeAllocation> employeeAllocations, int today) {
		this.employeeAllocations=employeeAllocations;
		this.today=today;
	}

	@Override
	public int compare(Employee o1, Employee o2) {
		// TODO debug this: full > parts (every group sorted by shifts)
		if(o1.isFulltime()) {
			if(o2.isFulltime()) {
				return compareByHadShiftYesterday(o1, o2);				
			} else {
				return -1;
			}
		} else {
			if(o2.isFulltime()) {
				return 1;
			} else {
				return compareByHadShiftYesterday(o1, o2);				
			}			
		}
	}

	private int compareByHadShiftYesterday(Employee o1, Employee o2) {
		boolean o1y = employeeAllocations.get(o1.getKey()).hadShiftYesterday(today);
		boolean o2y = employeeAllocations.get(o2.getKey()).hadShiftYesterday(today);
		if(o1y) {
			if(o2y) {
				return compareByShifts(o1, o2);
			} else {
				return 1;
			}			
		} else {
			if(o2y) {
				return -1;
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
