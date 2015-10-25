package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;

public class ComparatorAllocationByRole implements Comparator<EmployeeAllocation> {
	
	private ComparatorEmployeeByEditor e;
	private ComparatorEmployeeBySportak s;
	private ComparatorEmployeeByMortak m;
	
	public ComparatorAllocationByRole(boolean descending) {
		e=new ComparatorEmployeeByEditor(descending);
		s=new ComparatorEmployeeBySportak(descending);
		m=new ComparatorEmployeeByMortak(descending);
	}

	public int compare(EmployeeAllocation o1, EmployeeAllocation o2) {
		if(o1!=null && o2!=null) {
			int result;
			if((result=e.compare(o1.employee, o2.employee))==0) {
				if((result=s.compare(o1.employee, o2.employee))==0) {
					if((result=m.compare(o1.employee, o2.employee))==0) {
						return 0;
					} else {
						return result;
					}									
				} else {
					return result;
				}				
			} else {
				return result;
			}
		}
		return 0;
	}
}
