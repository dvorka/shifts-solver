package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.Employee;

public class ComparatorEmployeeByModified implements Comparator<Employee> {

	private int descending; 
	
	public ComparatorEmployeeByModified(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(Employee o1, Employee o2) {
		if(o1!=null && o2!=null) {
			return (int)(o1.getModified()-o2.getModified())*descending;
		}
		return 0;
	}
}
