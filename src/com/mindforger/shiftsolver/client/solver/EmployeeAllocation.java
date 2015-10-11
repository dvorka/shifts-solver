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
		if(isNight) {
			if(employee.isFulltime() && nights>=2) {
				throw new RuntimeException("Attemp to assign fulltime employee "+employee.getFullName()+" more than 2 night shifts (has "+nights+")");
			} else {
				nights++;				
			}
		}
	}
	
	public void unassign(boolean isNight) {
		shiftsOnDays.remove(shiftsOnDays.size()-1);
		shifts--;
		nights--;
	}

	public boolean hasCapacity(int day, boolean isNight) {
		return hasCapacity(day, isNight, 1, false);
	}
	
	public boolean hasCapacity(int day, boolean isNight, int capacityNeeded, boolean editorWeekendContinuity) {
		if(employee.isFulltime() && nights>=2 && isNight) {
			return false;
		}
		if(shiftsOnDays.size()>0) {
			// RULE: at most 1 shift/day
			if(shiftsOnDays.get(shiftsOnDays.size()-1)!=day || editorWeekendContinuity) {
				// RULE: at most 5 consecutive days at work (last 4 days connected to today)
				if(shiftsOnDays.size()>=5) {
					int lastIndex=shiftsOnDays.size()-1;
					int lastDay=shiftsOnDays.get(lastIndex);
					if(lastDay==(day-1)
						 &&
					   shiftsOnDays.get(lastIndex-1)==(lastDay-1)
						 &&
					   shiftsOnDays.get(lastIndex-2)==(lastDay-2)
						 &&
					   shiftsOnDays.get(lastIndex-3)==(lastDay-3)) 
					{
						return false;
					}
				}
				return hasCapacity(capacityNeeded);
			} else {
				return false;
			}
		} else {
			shiftsOnDays.add(day);
			return hasCapacity(capacityNeeded);
		}
	}

	private boolean hasCapacity(int capacityNeeded) {
		return shiftsToGet>0 && shiftsToGet>=(shifts+capacityNeeded);
	}
}
