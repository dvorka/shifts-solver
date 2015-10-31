package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;

public class ComparatorAllocationByFulltime implements Comparator<EmployeeAllocation> {

	private ComparatorEmployeeByFulltime c;
	
	public ComparatorAllocationByFulltime(boolean descending) {
		c=new ComparatorEmployeeByFulltime(descending);
	}

	public int compare(EmployeeAllocation o1, EmployeeAllocation o2) {
		if(o1!=null && o2!=null) {
			return c.compare(o1.employee, o2.employee); 
		}
		return 0;
	}
}
