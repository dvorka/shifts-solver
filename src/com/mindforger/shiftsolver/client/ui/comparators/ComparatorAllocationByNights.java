package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;

public class ComparatorAllocationByNights implements Comparator<EmployeeAllocation> {

	private int descending; 
	
	public ComparatorAllocationByNights(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(EmployeeAllocation o1, EmployeeAllocation o2) {
		if(o1!=null && o2!=null) {
			return (o1.nights-o2.nights)*descending;
		}
		return 0;
	}
}
