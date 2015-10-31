package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;

public class ComparatorAllocationByShifts implements Comparator<EmployeeAllocation> {

	private int descending; 
	
	public ComparatorAllocationByShifts(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(EmployeeAllocation o1, EmployeeAllocation o2) {
		if(o1!=null && o2!=null) {
			return (o1.shifts-o2.shifts)*descending;
		}
		return 0;
	}
}
