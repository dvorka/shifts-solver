package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;

public class ComparatorAllocationByName implements Comparator<EmployeeAllocation> {

	private int descending; 
	
	public ComparatorAllocationByName(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(EmployeeAllocation o1, EmployeeAllocation o2) {
		if(o1!=null && o2!=null) {
			return o1.employee.getFullName().compareTo(o2.employee.getFullName())*descending;
		}
		return 0;
	}
}
