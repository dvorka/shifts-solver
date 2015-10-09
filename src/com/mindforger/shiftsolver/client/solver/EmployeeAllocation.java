package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeAllocation {

	public Employee employee;
	public int stableArrayIndex;
	
	public int shiftsToGet;
	public int shifts;

	public List<Integer> shiftsOnDays;
	public int nights;
	
	public EmployeeAllocation(Employee employee, int workdaysInPeriod) {
		this.employee=employee;
		this.shifts=0;
		this.nights=0;
		this.shiftsOnDays=new ArrayList<Integer>();
		
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)workdaysInPeriod)/8f*7.5f);
		} else {
			shiftsToGet=Math.round((((float)workdaysInPeriod)/8f*7.5f)/2f);			
		}
	}

	public void assign(int day, boolean isNight) {
		shiftsOnDays.add(day);
		shifts++;
		nights++;
	}
	
	public void unassign(boolean isNight) {
		shiftsOnDays.remove(shiftsOnDays.size()-1);
		shifts--;
		nights--;
	}
	
	public boolean hasCapacity(int day, boolean isNight) {
		if(employee.isFulltime() && nights>=2) {
			return false;
		}
		if(shiftsOnDays.size()>0) {			
			if(shiftsOnDays.get(shiftsOnDays.size()-1)!=day) {
				// detecting 5 consecutive days
				if(shiftsOnDays.size()>=5) {
					int i=shiftsOnDays.size()-1;
					int lastDay=shiftsOnDays.get(i);
					if(shiftsOnDays.get(i-1)==(lastDay-1)
							&&
							shiftsOnDays.get(i-2)==(lastDay-2)
							&&
							shiftsOnDays.get(i-3)==(lastDay-3)
							&&
							shiftsOnDays.get(i-3)==(lastDay-3)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			shiftsOnDays.add(day);
		}
		return hasCapacity();
	}

	private boolean hasCapacity() {
		return shiftsToGet>0 && shiftsToGet>shifts;
	}
}
