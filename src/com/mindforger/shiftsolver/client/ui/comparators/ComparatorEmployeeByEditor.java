package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.Employee;

public class ComparatorEmployeeByEditor implements Comparator<Employee> {

	private int descending; 
	
	public ComparatorEmployeeByEditor(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(Employee o1, Employee o2) {
		if(o1!=null && o2!=null) {
			if(o1.isEditor()) {
				if(!o2.isEditor()) {
					return 1*descending;
				}
			} else {
				if(o2.isEditor()) {
					return -1*descending;
				}
			}
		}
		return 0;
	}
}
