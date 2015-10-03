package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.Employee;

public class ComparatorGrowBeanByName implements Comparator<Employee> {

	private int descending; 
	
	public ComparatorGrowBeanByName(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(Employee o1, Employee o2) {
		if(o1!=null && o2!=null) {
			return o1.getFullName().compareTo(o2.getFullName())*descending;
		}
		return 0;
	}
}
